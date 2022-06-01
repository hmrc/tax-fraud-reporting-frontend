#!/bin/bash

echo ""
echo "Applying migration BusinessCanNotFindAddress"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /businessCanNotFindAddress                       controllers.BusinessCanNotFindAddressController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "businessCanNotFindAddress.title = businessCanNotFindAddress" >> ../conf/messages.en
echo "businessCanNotFindAddress.heading = businessCanNotFindAddress" >> ../conf/messages.en

echo "Migration BusinessCanNotFindAddress completed"
