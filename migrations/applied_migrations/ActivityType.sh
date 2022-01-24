#!/bin/bash

echo ""
echo "Applying migration ActivityType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /activityType                        controllers.ActivityTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /activityType                        controllers.ActivityTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeActivityType                  controllers.ActivityTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeActivityType                  controllers.ActivityTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "activityType.title = activityType" >> ../conf/messages.en
echo "activityType.heading = activityType" >> ../conf/messages.en
echo "activityType.checkYourAnswersLabel = activityType" >> ../conf/messages.en
echo "activityType.error.required = Enter activityType" >> ../conf/messages.en
echo "activityType.error.length = ActivityType must be 100 characters or less" >> ../conf/messages.en
echo "activityType.change.hidden = ActivityType" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivityTypeUserAnswersEntry: Arbitrary[(ActivityTypePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ActivityTypePage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivityTypePage: Arbitrary[ActivityTypePage.type] =";\
    print "    Arbitrary(ActivityTypePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ActivityTypePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ActivityType completed"
