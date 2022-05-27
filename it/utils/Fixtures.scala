/*
package utils

import models.addresslookup.{AddressRecord, Country, RecordSet, Address => PafAddress}

trait PafFixtures {
  val exampleCountryUK = Country("UK", "United Kingdom")
  val subDivision = Some(Country("GB-ENG", "England"))

  val fakeStreetPafAddressRecord = AddressRecord(
    "GB101",
    PafAddress(
      List("1 Fake Street", "Fake Town", "Fake City"),
      Some("Fake Region"),
      None,
      "AA1 1AA",
      subDivision,
      exampleCountryUK
    ),
    "en"
  )

  val oneOtherPlacePafAddress =
    PafAddress(
      List("2 Other Place", "Some District"),
      Some("Anytown"),
      None,
      "AA1 1AA",
      subDivision,
      exampleCountryUK
    )
  val twoOtherPlacePafAddress =
    PafAddress(
      List("3 Other Place", "Some District"),
      Some("Anytown"),
      None,
      "AA1 1AA",
      Some(Country("GB-SCT", "Scotland")),
      exampleCountryUK
    )
  val otherPlacePafDifferentPostcodeAddress =
    PafAddress(
      List("3 Other Place", "Some District"),
      Some("Anytown"),
      None,
      "AA1 2AA",
      subDivision,
      exampleCountryUK
    )

  val oneOtherPlacePafAddressRecord = AddressRecord("GB990091234514", oneOtherPlacePafAddress, "en")
  val twoOtherPlacePafAddressRecord = AddressRecord("GB990091234515", twoOtherPlacePafAddress, "en")
  val otherPlacePafDifferentPostcodeAddressRecord =
    AddressRecord("GB990091234516", otherPlacePafDifferentPostcodeAddress, "en")

  val oneAndTwoOtherPlacePafRecordSet = RecordSet(
    List(
      oneOtherPlacePafAddressRecord,
      twoOtherPlacePafAddressRecord
    )
  )

  val newPostcodePlacePafRecordSet = RecordSet(
    List(
      otherPlacePafDifferentPostcodeAddressRecord
    )
  )

  val twoOtherPlaceRecordSet = RecordSet(
    List(twoOtherPlacePafAddressRecord)
  )
}*/
