#!/bin/bash

echo ""
echo "Applying migration ConfirmAddress"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /confirmAddress                       controllers.ConfirmAddressController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "confirmAddress.title = confirmAddress" >> ../conf/messages.en
echo "confirmAddress.heading = confirmAddress" >> ../conf/messages.en

echo "Migration ConfirmAddress completed"
