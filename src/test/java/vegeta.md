# vegeta load testing
`echo "GET http://localhost:8888/servicedefinitions/" | vegeta attack -workers=4 max-workers=10 -duration=30s | tee results.bin | vegeta report
