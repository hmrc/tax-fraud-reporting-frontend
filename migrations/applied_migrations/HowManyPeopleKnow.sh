#!/bin/bash

echo ""
echo "Applying migration HowManyPeopleKnow"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /howManyPeopleKnow                        controllers.HowManyPeopleKnowController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /howManyPeopleKnow                        controllers.HowManyPeopleKnowController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHowManyPeopleKnow                  controllers.HowManyPeopleKnowController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHowManyPeopleKnow                  controllers.HowManyPeopleKnowController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "howManyPeopleKnow.title = How many other people know about the activity?" >> ../conf/messages.en
echo "howManyPeopleKnow.heading = How many other people know about the activity?" >> ../conf/messages.en
echo "howManyPeopleKnow.myselfAndIndividual = MyselfAndIndividual" >> ../conf/messages.en
echo "howManyPeopleKnow.oneToFiveIndividuals = OneToFiveIndividuals" >> ../conf/messages.en
echo "howManyPeopleKnow.checkYourAnswersLabel = How many other people know about the activity?" >> ../conf/messages.en
echo "howManyPeopleKnow.error.required = Select howManyPeopleKnow" >> ../conf/messages.en
echo "howManyPeopleKnow.change.hidden = HowManyPeopleKnow" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHowManyPeopleKnowUserAnswersEntry: Arbitrary[(HowManyPeopleKnowPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[HowManyPeopleKnowPage.type]";\
    print "        value <- arbitrary[HowManyPeopleKnow].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHowManyPeopleKnowPage: Arbitrary[HowManyPeopleKnowPage.type] =";\
    print "    Arbitrary(HowManyPeopleKnowPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryHowManyPeopleKnow: Arbitrary[HowManyPeopleKnow] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(HowManyPeopleKnow.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(HowManyPeopleKnowPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration HowManyPeopleKnow completed"
