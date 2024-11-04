package com.ademarporto.ls.model;

import java.util.List;
import java.util.UUID;

public record Client(UUID clientId,
                     String name,
                     List<Loan> loans) {
}
