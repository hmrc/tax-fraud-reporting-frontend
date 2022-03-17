#!/bin/bash

echo ""
echo "Applying migration BetaBanner"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /betaBanner                       controllers.BetaBannerController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "betaBanner.title = betaBanner" >> ../conf/messages.en
echo "betaBanner.heading = betaBanner" >> ../conf/messages.en

echo "Migration BetaBanner completed"
