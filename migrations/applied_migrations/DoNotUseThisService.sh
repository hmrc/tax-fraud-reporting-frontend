#!/bin/bash

echo ""
echo "Applying migration DoNotUseThisService"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /doNotUseThisService                       controllers.DoNotUseThisServiceController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "doNotUseThisService.title = doNotUseThisService" >> ../conf/messages.en
echo "doNotUseThisService.heading = doNotUseThisService" >> ../conf/messages.en

echo "Migration DoNotUseThisService completed"
