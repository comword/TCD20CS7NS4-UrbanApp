package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"sync"
	"time"

	firebase "firebase.google.com/go"
	"firebase.google.com/go/db"

	"google.golang.org/api/option"
)

var ctx context.Context

var httpClient = &http.Client{Timeout: 10 * time.Second}

func aqiData(db *db.Client, ref *db.Ref) {
	type AQIData struct {
		Generated string
	}
	lastData := new(AQIData)
	dataRef := ref.Child("AQIData")
	ticker := time.NewTicker(time.Minute * 5)
	defer ticker.Stop()
	for {
		fmt.Println("Sending request to AQI API")
		resp, err := httpClient.Get("https://erc.epa.ie/air/services/aqih/aqih_dev.php")
		if err != nil {
			log.Fatal(err)
			resp.Body.Close()
		}

		if resp.StatusCode == http.StatusOK {
			bodyBytes, err := ioutil.ReadAll(resp.Body)
			if err != nil {
				log.Fatal(err)
				resp.Body.Close()
			}
			bodyString := string(bodyBytes)[1:]
			bodyString = bodyString[:len(bodyString)-3]
			// bodyString = strings.TrimRight(bodyString, "]\n")
			prevTime := lastData.Generated
			err = json.Unmarshal([]byte(bodyString), &lastData)
			if err != nil {
				log.Fatal(err)
			}
			if prevTime != lastData.Generated {
				fmt.Println("AQIData: Old time:", prevTime, ", Last time: ", lastData.Generated)
				fmt.Println(bodyString)
				mRef := dataRef.Child(lastData.Generated)
				aMap := make(map[string]interface{})
				err := json.Unmarshal([]byte(bodyString), &aMap)
				if err != nil {
					log.Fatalln(err)
				}
				err = mRef.Set(ctx, aMap)
				if err != nil {
					log.Fatalln("Error setting value:", err)
				}
				// update latest
				latestRef := ref.Child("AQIData-latest")
				err = latestRef.Set(ctx, aMap)
				if err != nil {
					log.Fatalln("Error setting value:", err)
				}
			}

		}

		if err != nil {
			log.Println(err)
		}
		select {
		case <-ticker.C:
			continue
		}
	}
}

func weatherData(db *db.Client, ref *db.Ref) {
	type WeatherData struct {
		Name                  string
		Temperature           string
		Symbol                string
		WeatherDescription    string
		Text                  string
		WindSpeed             string
		WindGust              string
		CardinalWindDirection string
		WindDirection         int
		Humidity              string
		Rainfall              string
		Pressure              string
		DayName               string
		Date                  string
		ReportTime            string
	}
	var lastData []WeatherData
	dataRef := ref.Child("DublinToday")
	ticker := time.NewTicker(time.Minute * 30)
	defer ticker.Stop()
	for {
		fmt.Println("Sending request to weather API")
		resp, err := httpClient.Get("https://prodapi.metweb.ie/observations/dublin/today")
		if err != nil {
			log.Fatal(err)
			resp.Body.Close()
		}

		if resp.StatusCode == http.StatusOK {
			bodyBytes, err := ioutil.ReadAll(resp.Body)
			if err != nil {
				log.Fatal(err)
				resp.Body.Close()
			}
			bodyString := string(bodyBytes)
			prevTime := ""
			if len(lastData) != 0 {
				last := lastData[len(lastData)-1]
				prevTime = last.ReportTime + " " + last.Date
			}
			err = json.Unmarshal([]byte(bodyString), &lastData)
			if err != nil {
				log.Fatal(err)
			}
			last := lastData[len(lastData)-1]
			thisTime := last.ReportTime + " " + last.Date
			if thisTime != prevTime {
				fmt.Println("Upload the weather data at", thisTime)
				mRef := dataRef.Child(last.Date)
				err = mRef.Set(ctx, lastData)
				if err != nil {
					log.Fatalln("Error setting value:", err)
				}
				// update latest
				latestRef := ref.Child("DublinToday-latest")
				err = latestRef.Set(ctx, lastData)
				if err != nil {
					log.Fatalln("Error setting value:", err)
				}
			}

		}

		if err != nil {
			log.Println(err)
		}
		select {
		case <-ticker.C:
			continue
		}
	}
}

func initFirebase(keyPath string, dbURL string) (*db.Client, error) {
	conf := &firebase.Config{
		DatabaseURL: "https://" + dbURL,
	}
	opt := option.WithCredentialsFile(keyPath)
	app, err := firebase.NewApp(ctx, conf, opt)
	if err != nil {
		return nil, fmt.Errorf("error initializing app: %v", err)
	}
	client, err := app.Database(ctx)
	if err != nil {
		return nil, fmt.Errorf("Error initializing database client: %v", err)
	}
	return client, nil
}

func main() {
	configPath := flag.String("config", "config.json", "Path to the Firebase key config file")
	dbURL := flag.String("dburl", "tcdcs-2020.firebaseio.com", "URL to the Firebase database")
	flag.Parse()

	ctx = context.Background()
	client, err := initFirebase(*configPath, *dbURL)
	if err != nil {
		panic(err)
	}
	ref := client.NewRef("open-data/")

	var wg sync.WaitGroup

	fmt.Println("Main: Starting AQI worker")
	wg.Add(1)
	go aqiData(client, ref)

	fmt.Println("Main: Starting Weather worker")
	wg.Add(1)
	go weatherData(client, ref)

	fmt.Println("Main: Started")
	wg.Wait()
}
