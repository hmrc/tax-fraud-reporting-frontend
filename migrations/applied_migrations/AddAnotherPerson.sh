#!/bin/bash

echo ""
echo "Applying migration AddAnotherPerson"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /addAnotherPerson                        controllers.AddAnotherPersonController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /addAnotherPerson                        controllers.AddAnotherPersonController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAddAnotherPerson                  controllers.AddAnotherPersonController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAddAnotherPerson                  controllers.AddAnotherPersonController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "addAnotherPerson.title = Are there additional people involved in this activity you want to tell us about?" >> ../conf/messages.en
echo "addAnotherPerson.heading = Are there additional people involved in this activity you want to tell us about?" >> ../conf/messages.en
echo "addAnotherPerson.yes = Yeyes" >> ../conf/messages.en
echo "addAnotherPerson.no = no" >> ../conf/messages.en
echo "addAnotherPerson.checkYourAnswersLabel = Are there additional people involved in this activity you want to tell us about?" >> ../conf/messages.en
echo "addAnotherPerson.error.required = Select addAnotherPerson" >> ../conf/messages.en
echo "addAnotherPerson.change.hidden = AddAnotherPerson" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAnotherPersonUserAnswersEntry: Arbitrary[(AddAnotherPersonPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AddAnotherPersonPage.type]";\
    print "        value <- arbitrary[AddAnotherPerson].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAnotherPersonPage: Arbitrary[AddAnotherPersonPage.type] =";\
    print "    Arbitrary(AddAnotherPersonPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to ModelGenerators"
awk '/trait ModelGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAddAnotherPerson: Arbitrary[AddAnotherPerson] =";\
    print "    Arbitrary {";\
    print "      Gen.oneOf(AddAnotherPerson.values.toSeq)";\
    print "    }";\
    next }1' ../test/generators/ModelGenerators.scala > tmp && mv tmp ../test/generators/ModelGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AddAnotherPersonPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Migration AddAnotherPerson completed"
