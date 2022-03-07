#!/bin/bash

echo ""
echo "Applying migration ServiceTimeout"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /serviceTimeout                       controllers.ServiceTimeoutController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "serviceTimeout.title = serviceTimeout" >> ../conf/messages.en
echo "serviceTimeout.heading = serviceTimeout" >> ../conf/messages.en

echo "Migration ServiceTimeout completed"
