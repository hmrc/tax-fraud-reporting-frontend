#!/bin/bash

echo ""
echo "Applying migration IndividualAddressConfirmation"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /individualAddressConfirmation                       controllers.IndividualAddressConfirmationController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualAddressConfirmation.title = individualAddressConfirmation" >> ../conf/messages.en
echo "individualAddressConfirmation.heading = individualAddressConfirmation" >> ../conf/messages.en

echo "Migration IndividualAddressConfirmation completed"
