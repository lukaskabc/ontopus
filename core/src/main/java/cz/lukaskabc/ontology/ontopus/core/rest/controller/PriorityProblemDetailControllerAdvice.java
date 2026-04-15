package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class PriorityProblemDetailControllerAdvice {}
