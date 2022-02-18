#!/bin/bash

echo ""
echo "Applying migration IndividualConfirmRemove"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualConfirmRemove                        controllers.IndividualConfirmRemoveController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualConfirmRemove                        controllers.IndividualConfirmRemoveController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualConfirmRemove                  controllers.IndividualConfirmRemoveController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualConfirmRemove                  controllers.IndividualConfirmRemoveController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualConfirmRemove.title = individualConfirmRemove" >> ../conf/messages.en
echo "individualConfirmRemove.heading = individualConfirmRemove" >> ../conf/messages.en
echo "individualConfirmRemove.checkYourAnswersLabel = individualConfirmRemove" >> ../conf/messages.en
echo "individualConfirmRemove.error.required = Select yes if individualConfirmRemove" >> ../conf/messages.en
echo "individualConfirmRemove.change.hidden = IndividualConfirmRemove" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualConfirmRemoveUserAnswersEntry: Arbitrary[(IndividualConfirmRemovePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualConfirmRemovePage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualConfirmRemovePage: Arbitrary[IndividualConfirmRemovePage.type] =";\
    print "    Arbitrary(IndividualConfirmRemovePage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualConfirmRemovePage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration IndividualConfirmRemove completed"
