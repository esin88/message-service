package com.example.message.service.message;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Disabled;

@Disabled("test for native image is disabled")
@NativeImageTest
public class NativeMessageControllerIT extends MessageControllerTest {

    // Execute the same tests but in native mode.
}
