#!/bin/bash

echo ""
echo "Applying migration CanNotFindAddress"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /canNotFindAddress                       controllers.CanNotFindAddressController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "canNotFindAddress.title = canNotFindAddress" >> ../conf/messages.en
echo "canNotFindAddress.heading = canNotFindAddress" >> ../conf/messages.en

echo "Migration CanNotFindAddress completed"
