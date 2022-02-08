#!/bin/bash

echo ""
echo "Applying migration WhenActivityHappen"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /whenActivityHappen                        controllers.WhenActivityHappenController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /whenActivityHappen                        controllers.WhenActivityHappenController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeWhenActivityHappen                  controllers.WhenActivityHappenController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeWhenActivityHappen                  controllers.WhenActivityHappenController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "whenActivityHappen.title = What is the length of time this activity has been going on for?" >> ../conf/messages.en
echo "whenActivityHappen.heading = What is the length of time this activity has been going on for?" >> ../conf/messages.en
echo "whenActivityHappen.overFiveYears = OverFiveYears" >> ../conf/messages.en
echo "whenActivityHappen.betweenOneAndFive = BetweenOneAndFive" >> ../conf/messages.en
echo "whenActivityHappen.checkYourAnswersLabel = What is the length of time this activity has been going on for?" >> ../conf/messages.en
echo "whenActivityHappen.error.required = Select whenActivityHappen" >> ../conf/messages.en
echo "whenActivityHappen.change.hidden = WhenActivityHappen" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenActivityHappenUserAnswersEntry: Arbitrary[(WhenActivityHappenPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[WhenActivityHappenPage.type]";\
    print "        value <- arbitrary[WhenActivityHappen].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenActivityHappenPage: Arbitrary[WhenActivityHappenPage.type] =";\
    print "    Arbitrary(WhenActivityHappenPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryWhenActivityHappen: Arbitrary[WhenActivityHappen] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(WhenActivityHappen.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(WhenActivityHappenPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration WhenActivityHappen completed"
