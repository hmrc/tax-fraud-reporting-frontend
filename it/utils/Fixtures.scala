package utils

import models.addresslookup.{Country, ProposedAddress}

trait PafFixtures {
  val exampleCountryUK = Country("UK", "United Kingdom")
  val subDivision      = Some(Country("GB-ENG", "England"))

  val matchedAddress = List(
    ProposedAddress(
      "GB990091234514",
      None,
      None,
      None,
      None,
      Some("AA1 1AA"),
      Some("Anytown"),
      List("2 Other Place", "Some District"),
      Country("GB", "United Kingdom"),
      None
    ),
    ProposedAddress(
      "GB990091234515",
      None,
      None,
      None,
      None,
      Some("AA1 1AA"),
      Some("Anytown"),
      List("3 Other Place", "Some District"),
      Country("GB", "United Kingdom"),
      None
    )
  )

  val oneAndTwoOtherPlacePafRecordSet = List(
    ProposedAddress(
      "GB990091234514",
      None,
      None,
      None,
      None,
      Some("AA1 1AA"),
      Some("Anytown"),
      List("2 Other Place", "Some District"),
      Country("GB", "United Kingdom"),
      None
    ),
    ProposedAddress(
      "GB990091234515",
      None,
      None,
      None,
      None,
      Some("AA1 1AA"),
      Some("Anytown"),
      List("3 Other Place", "Some District"),
      Country("GB", "United Kingdom"),
      None
    )
  )

  val twoOtherPlaceRecordSet = List(ProposedAddress("GB990091234515",None,None,None,None,
    Some("AA1 1AA"),Some("Anytown"),List("3 Other Place", "Some District"),
    Country("GB","United Kingdom"),None),
    ProposedAddress("GB990091234514",None,None,None,None,Some("AA1 1AA"),Some("Anytown"),List(),Country("GB","United Kingdom"),None))

}
