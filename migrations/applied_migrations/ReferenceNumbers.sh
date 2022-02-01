#!/bin/bash

echo ""
echo "Applying migration ReferenceNumbers"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /referenceNumbers                        controllers.ReferenceNumbersController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /referenceNumbers                        controllers.ReferenceNumbersController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeReferenceNumbers                  controllers.ReferenceNumbersController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeReferenceNumbers                  controllers.ReferenceNumbersController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "referenceNumbers.title = referenceNumbers" >> ../conf/messages.en
echo "referenceNumbers.heading = referenceNumbers" >> ../conf/messages.en
echo "referenceNumbers.checkYourAnswersLabel = referenceNumbers" >> ../conf/messages.en
echo "referenceNumbers.error.required = Enter referenceNumbers" >> ../conf/messages.en
echo "referenceNumbers.error.length = ReferenceNumbers must be 1001100 characters or less" >> ../conf/messages.en
echo "referenceNumbers.change.hidden = ReferenceNumbers" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReferenceNumbersUserAnswersEntry: Arbitrary[(ReferenceNumbersPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[ReferenceNumbersPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryReferenceNumbersPage: Arbitrary[ReferenceNumbersPage.type] =";\
    print "    Arbitrary(ReferenceNumbersPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(ReferenceNumbersPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration ReferenceNumbers completed"
