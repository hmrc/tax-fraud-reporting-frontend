#!/bin/bash

echo ""
echo "Applying migration DocumentationDescription"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /documentationDescription                        controllers.DocumentationDescriptionController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /documentationDescription                        controllers.DocumentationDescriptionController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeDocumentationDescription                  controllers.DocumentationDescriptionController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeDocumentationDescription                  controllers.DocumentationDescriptionController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "documentationDescription.title = documentationDescription" >> ../conf/messages.en
echo "documentationDescription.heading = documentationDescription" >> ../conf/messages.en
echo "documentationDescription.checkYourAnswersLabel = documentationDescription" >> ../conf/messages.en
echo "documentationDescription.error.required = Enter documentationDescription" >> ../conf/messages.en
echo "documentationDescription.error.length = DocumentationDescription must be 50 characters or less" >> ../conf/messages.en
echo "documentationDescription.change.hidden = DocumentationDescription" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDocumentationDescriptionUserAnswersEntry: Arbitrary[(DocumentationDescriptionPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[DocumentationDescriptionPage.type]";\
    print "        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test-utils/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test-utils/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryDocumentationDescriptionPage: Arbitrary[DocumentationDescriptionPage.type] =";\
    print "    Arbitrary(DocumentationDescriptionPage)";\
    next }1' ../test-utils/generators/PageGenerators.scala > tmp && mv tmp ../test-utils/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(DocumentationDescriptionPage.type, JsValue)] ::";\
    next }1' ../test-utils/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test-utils/generators/UserAnswersGenerator.scala

echo "Migration DocumentationDescription completed"
