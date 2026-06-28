package com.mall.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoginTokenCodecTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void encodeAndDecode_withSameSecret_returnsLoginToken() {
        final LoginTokenDTO loginToken = new LoginTokenDTO();
        loginToken.setAccountId(700001L);
        loginToken.setUsername("test_platform_admin");
        loginToken.setPortalCode("admin");
        loginToken.setAuthorities(List.of("system:menu:view", "wms:inbound:view"));
        loginToken.setAllPlatform(true);
        loginToken.setWarehouseIds(List.of(50001L));
        final LoginTokenCodec codec = new LoginTokenCodec(objectMapper, "test-secret-32-characters-value");

        final String encoded = codec.encode(loginToken);
        final LoginTokenDTO decoded = codec.decode(encoded);

        assertEquals(700001L, decoded.getAccountId());
        assertEquals("admin", decoded.getPortalCode());
        assertTrue(decoded.getAuthorities().contains("system:menu:view"));
        assertTrue(decoded.isAllPlatform());
        assertTrue(decoded.getWarehouseIds().contains(50001L));
    }

    @Test
    void decode_withDifferentSecret_returnsNull() {
        final LoginTokenDTO loginToken = new LoginTokenDTO();
        loginToken.setAccountId(700001L);
        final String encoded = new LoginTokenCodec(objectMapper, "test-secret-32-characters-value").encode(loginToken);

        final LoginTokenDTO decoded =
                new LoginTokenCodec(objectMapper, "another-secret-32-characters-val").decode(encoded);

        assertNull(decoded);
    }

    @Test
    void constructor_blankSecret_throwException() {
        assertThrows(IllegalArgumentException.class, () -> new LoginTokenCodec(objectMapper, ""));
    }
}
