#!/bin/bash

echo ""
echo "Applying migration SubmitYourReport"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /submitYourReport                       controllers.SubmitYourReportController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "submitYourReport.title = submitYourReport" >> ../conf/messages.en
echo "submitYourReport.heading = submitYourReport" >> ../conf/messages.en

echo "Migration SubmitYourReport completed"
