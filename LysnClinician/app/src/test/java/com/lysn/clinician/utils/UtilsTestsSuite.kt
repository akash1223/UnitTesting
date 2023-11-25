package com.lysn.clinician.utils


import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    CryptographyTest::class,
    NetworkManagerTest::class,
    LocalizeTextProviderTest::class,
    SingletonHolderTest::class,
    ValidatorTest::class,
    BindingAdaptersTest::class,
    MixPanelDataTest::class
)
class UtilsTestsSuite