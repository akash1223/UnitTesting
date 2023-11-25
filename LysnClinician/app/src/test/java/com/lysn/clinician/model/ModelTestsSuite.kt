package com.lysn.clinician.model

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(

    ConsultationsDetailsResponseTest::class,
    VideoSessionTokenResponseTest::class
)
class ModelTestsSuite