package org.ieeeihuserres.certificateserver.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Participant {
    private final String firstName;
    private final String lastName;
    private final String email;

}
