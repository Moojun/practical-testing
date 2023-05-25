package com.inflearn.practical_testing.unit.beverage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {

    @Test
    void getName() {
        Americano americano = new Americano();

//        assertEquals(americano.getName(), "아메리카노");   // junit
        assertThat(americano.getName()).isEqualTo("아메리카노"); // assertj
    }

    @Test
    void getPrice() {
        Americano americano = new Americano();

        assertThat(americano.getPrice()).isEqualTo(4000);
    }

}