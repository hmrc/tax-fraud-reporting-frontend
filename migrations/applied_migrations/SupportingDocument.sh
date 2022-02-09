#!/bin/bash

echo ""
echo "Applying migration SupportingDocument"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /supportingDocument                        controllers.SupportingDocumentController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /supportingDocument                        controllers.SupportingDocumentController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSupportingDocument                  controllers.SupportingDocumentController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSupportingDocument                  controllers.SupportingDocumentController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "supportingDocument.title = Do you have any supporting information?" >> ../conf/messages.en
echo "supportingDocument.heading = Do you have any supporting information?" >> ../conf/messages.en
echo "supportingDocument.yes = Yes" >> ../conf/messages.en
echo "supportingDocument.no = No" >> ../conf/messages.en
echo "supportingDocument.checkYourAnswersLabel = Do you have any supporting information?" >> ../conf/messages.en
echo "supportingDocument.error.required = Select supportingDocument" >> ../conf/messages.en
echo "supportingDocument.change.hidden = SupportingDocument" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingDocumentUserAnswersEntry: Arbitrary[(SupportingDocumentPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[SupportingDocumentPage.type]";\
    print "        value <- arbitrary[SupportingDocument].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingDocumentPage: Arbitrary[SupportingDocumentPage.type] =";\
    print "    Arbitrary(SupportingDocumentPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitrarySupportingDocument: Arbitrary[SupportingDocument] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(SupportingDocument.values.toSeq)";\
    print "    }";\
    next }1' ../test-utils/generators/ModelGenerators.scala > tmp && mv tmp ../test-utils/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(SupportingDocumentPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration SupportingDocument completed"
