package com.dvarela.expenses.api;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

final class Problems {

    static final HttpStatusCode UNPROCESSABLE_CONTENT = HttpStatusCode.valueOf(422);

    private Problems() {
    }

    static ProblemDetail of(HttpStatusCode status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}