package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** @see PriorityProblemDetailControllerAdvice */
@Order(1)
@ControllerAdvice
public class ProblemDetailControllerAdvice extends ResponseEntityExceptionHandler {}
