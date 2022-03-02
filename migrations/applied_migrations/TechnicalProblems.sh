#!/bin/bash

echo ""
echo "Applying migration TechnicalProblems"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /technicalProblems                       controllers.TechnicalProblemsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "technicalProblems.title = technicalProblems" >> ../conf/messages.en
echo "technicalProblems.heading = technicalProblems" >> ../conf/messages.en

echo "Migration TechnicalProblems completed"
