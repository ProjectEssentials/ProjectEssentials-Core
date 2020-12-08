package com.projectessentials.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MetaKtTest : FunSpec({
    test("implVersion") {
        implVersion().shouldBe(-1)
    }

    test("implName") {
        implName().shouldBe("unknown")
    }
})
