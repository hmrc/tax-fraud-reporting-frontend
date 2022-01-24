#!/bin/bash

echo ""
echo "Applying migration IndividualName"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /individualName                        controllers.IndividualNameController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /individualName                        controllers.IndividualNameController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeIndividualName                  controllers.IndividualNameController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeIndividualName                  controllers.IndividualNameController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "individualName.title = individualName" >> ../conf/messages.en
echo "individualName.heading = individualName" >> ../conf/messages.en
echo "individualName.firstName = firstName" >> ../conf/messages.en
echo "individualName.middleName = middleName" >> ../conf/messages.en
echo "individualName.checkYourAnswersLabel = IndividualName" >> ../conf/messages.en
echo "individualName.error.firstName.required = Enter firstName" >> ../conf/messages.en
echo "individualName.error.middleName.required = Enter middleName" >> ../conf/messages.en
echo "individualName.error.firstName.length = firstName must be 100 characters or less" >> ../conf/messages.en
echo "individualName.error.middleName.length = middleName must be 100 characters or less" >> ../conf/messages.en
echo "individualName.firstName.change.hidden = firstName" >> ../conf/messages.en
echo "individualName.middleName.change.hidden = middleName" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualNameUserAnswersEntry: Arbitrary[(IndividualNamePage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[IndividualNamePage.type]";\
    print "        value <- arbitrary[IndividualName].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualNamePage: Arbitrary[IndividualNamePage.type] =";\
    print "    Arbitrary(IndividualNamePage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryIndividualName: Arbitrary[IndividualName] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        firstName <- arbitrary[String]";\
    print "        middleName <- arbitrary[String]";\
    print "      } yield IndividualName(firstName, middleName)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(IndividualNamePage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration IndividualName completed"
