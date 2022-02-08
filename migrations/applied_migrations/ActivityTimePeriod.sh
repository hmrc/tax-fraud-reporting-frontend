#!/bin/bash

echo ""
echo "Applying migration ActivityTimePeriod"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /activityTimePeriod                        controllers.ActivityTimePeriodController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /activityTimePeriod                        controllers.ActivityTimePeriodController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeActivityTimePeriod                  controllers.ActivityTimePeriodController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeActivityTimePeriod                  controllers.ActivityTimePeriodController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "activityTimePeriod.title = When will the activity likely happen?" >> ../conf/messages.en
echo "activityTimePeriod.heading = When will the activity likely happen?" >> ../conf/messages.en
echo "activityTimePeriod.nextWeek = NextWeek" >> ../conf/messages.en
echo "activityTimePeriod.threeMonths = ThreeMonths" >> ../conf/messages.en
echo "activityTimePeriod.checkYourAnswersLabel = When will the activity likely happen?" >> ../conf/messages.en
echo "activityTimePeriod.error.required = Select activityTimePeriod" >> ../conf/messages.en
echo "activityTimePeriod.change.hidden = ActivityTimePeriod" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivityTimePeriodUserAnswersEntry: Arbitrary[(ActivityTimePeriodPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ActivityTimePeriodPage.type]";\
    print "        value <- arbitrary[ActivityTimePeriod].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivityTimePeriodPage: Arbitrary[ActivityTimePeriodPage.type] =";\
    print "    Arbitrary(ActivityTimePeriodPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryActivityTimePeriod: Arbitrary[ActivityTimePeriod] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(ActivityTimePeriod.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ActivityTimePeriodPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ActivityTimePeriod completed"
