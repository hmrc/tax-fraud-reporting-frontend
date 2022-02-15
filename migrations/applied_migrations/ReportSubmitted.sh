#!/bin/bash

echo ""
echo "Applying migration ReportSubmitted"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /reportSubmitted                       controllers.ReportSubmittedController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "reportSubmitted.title = reportSubmitted" >> ../conf/messages.en
echo "reportSubmitted.heading = reportSubmitted" >> ../conf/messages.en

echo "Migration ReportSubmitted completed"
