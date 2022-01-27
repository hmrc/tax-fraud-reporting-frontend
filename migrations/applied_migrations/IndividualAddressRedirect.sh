#!/bin/bash

echo ""
echo "Applying migration IndividualAddressRedirect"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /individualAddressRedirect                       controllers.IndividualAddressRedirectController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualAddressRedirect.title = individualAddressRedirect" >> ../conf/messages.en
echo "individualAddressRedirect.heading = individualAddressRedirect" >> ../conf/messages.en

echo "Migration IndividualAddressRedirect completed"
