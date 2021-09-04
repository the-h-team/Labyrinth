package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Experimental;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.library.Message;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public class Check {

	public static void argument(boolean b, @Nullable String message) {
		if (!b) {
			throw new IllegalArgumentException(message);
		}
	}

	public static <T> T forNull(T t) {
		if (t == null) throw new IllegalArgumentException("Value cannot be null!");
		return forWarnings(t);
	}

	public static <T> T forWarnings(T t) {
		if (t == null) throw new IllegalArgumentException("Value cannot be null!");
		AnnotationDiscovery<Experimental, Object> discovery = AnnotationDiscovery.of(Experimental.class, t);
		discovery.filter(m -> Arrays.stream(m.getParameters()).anyMatch(p -> p.isAnnotationPresent(Experimental.class)) || m.isAnnotationPresent(Experimental.class));
		if (discovery.isPresent()) {
			Message message = LabyrinthProvider.getService(Service.MESSENGER).getNewMessage();
			message.warn("- Warning scan found (" + discovery.count() + ") methods at checkout.");
			discovery.ifPresent((r, m) -> {
				message.warn("- Method " + m.getName() + " found with warning '" + r.value() + "'");
			});
		} else {
			if (t.getClass().isAnnotationPresent(Experimental.class)) {
				Message message = LabyrinthProvider.getService(Service.MESSENGER).getNewMessage();
				Experimental e = t.getClass().getAnnotation(Experimental.class);
				message.warn("- Class " + t.getClass().getSimpleName() + " found with warning '" + e.value() + "'");
			}
		}
		return t;
	}

	public static <A extends Annotation, T> T forAnnotation(T t, Class<A> annotative, AnnotationDiscovery.AnnotativeConsumer<A, Method, String> function) {
		return forAnnotation(t, annotative, function, false);
	}

	public static <A extends Annotation, T> T forAnnotation(T t, Class<A> annotative, AnnotationDiscovery.AnnotativeConsumer<A, Method, String> function, boolean warning) {
		if (t == null) throw new IllegalArgumentException("Value cannot be null!");
		AnnotationDiscovery<A, Object> discovery = AnnotationDiscovery.of(annotative, t);
		discovery.filter(m -> Arrays.stream(m.getParameters()).anyMatch(p -> p.isAnnotationPresent(annotative)) || m.isAnnotationPresent(annotative));
		if (discovery.isPresent()) {
			Message message = LabyrinthProvider.getService(Service.MESSENGER).getNewMessage();
			if (warning) {
				message.info("- Warning scan found (" + discovery.count() + ") methods at checkout.");
				discovery.ifPresent((r, m) -> message.warn(function.accept(r, m)));
			} else {
				message.info("- Info scan found (" + discovery.count() + ") methods at checkout.");
				discovery.ifPresent((r, m) -> message.info(function.accept(r, m)));
			}
		} else {
			if (t.getClass().isAnnotationPresent(annotative)) {
				Message message = LabyrinthProvider.getService(Service.MESSENGER).getNewMessage();
				A e = t.getClass().getAnnotation(annotative);
				if (warning) {
					message.warn(function.accept(e, t.getClass().getMethods()[0]));
				} else {
					message.info(function.accept(e, t.getClass().getMethods()[0]));
				}
			}
		}
		return t;
	}


}
