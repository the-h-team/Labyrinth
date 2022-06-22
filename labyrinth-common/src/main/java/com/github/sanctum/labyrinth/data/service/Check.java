package com.github.sanctum.labyrinth.data.service;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Experimental;
import com.github.sanctum.labyrinth.annotation.Json;
import com.github.sanctum.labyrinth.annotation.Note;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.library.Mailer;
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

	public static @Json boolean isJson(String string) {
		return string.startsWith("{") || string.startsWith("[") && string.endsWith("{") || string.endsWith("[");
	}

	public static @Json String forJson(String string, String message) {
		if (!isJson(string)) {
			throw new IllegalArgumentException(message);
		}
		return string;
	}

	public static boolean isNull(Object... o) {
		for (Object ob : o) {
			if (ob != null) return false;
		}
		return true;
	}

	public static <T> T forNull(T t) {
		if (t == null) throw new NullPointerException("Value cannot be null!");
		return forWarnings(t);
	}

	public static <T> T forNull(T t, String message) {
		if (t == null) throw new NullPointerException(message);
		return forWarnings(t);
	}

	public static <T> T forWarnings(T t) {
		if (t == null) throw new IllegalArgumentException("Value cannot be null!");
		AnnotationDiscovery<Experimental, Object> discovery = AnnotationDiscovery.of(Experimental.class, t);
		discovery.filter(m -> Arrays.stream(m.getParameters()).anyMatch(p -> p.isAnnotationPresent(Experimental.class)) || m.isAnnotationPresent(Experimental.class));
		AnnotationDiscovery<Note, Object> discovery2 = AnnotationDiscovery.of(Note.class, t);
		discovery2.filter(m -> Arrays.stream(m.getParameters()).anyMatch(p -> p.isAnnotationPresent(Note.class)) || m.isAnnotationPresent(Note.class));
		if (discovery.isPresent()) {
			Mailer message = LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer();
			message.warn("- Warning scan found (" + discovery.count() + ") methods at checkout for object '" + t.getClass().getSimpleName() + "'").queue();
			if (t.getClass().isAnnotationPresent(Experimental.class)) {
				Experimental e = t.getClass().getAnnotation(Experimental.class);
				message.warn("- Entire class " + t.getClass().getSimpleName() + " found with warning '" + e.dueTo() + "'").queue();
			}
			discovery.ifPresent((r, m) -> {
				message.warn("- Method " + m.getName() + " found with warning '" + r.dueTo() + "'").queue();
			});
			discovery2.ifPresent((r, m) -> {
				message.info("- Method " + m.getName() + " found with note '" + r.value() + "'").queue();
			});
		} else {
			if (t.getClass().isAnnotationPresent(Experimental.class)) {
				Mailer message = LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer();
				Experimental e = t.getClass().getAnnotation(Experimental.class);
				message.warn("- Class " + t.getClass().getSimpleName() + " found with warning '" + e.dueTo() + "'").queue();
			}
			if (t.getClass().isAnnotationPresent(Note.class)) {
				Mailer message = LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer();
				Note e = t.getClass().getAnnotation(Note.class);
				message.info("- Class " + t.getClass().getSimpleName() + " found with note '" + e.value() + "'").queue();
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
			Mailer message = LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer();
			if (warning) {
				message.info("- Warning scan found (" + discovery.count() + ") methods at checkout.").queue();
				discovery.ifPresent((r, m) -> message.warn(function.accept(r, m)));
			} else {
				message.info("- Info scan found (" + discovery.count() + ") methods at checkout.").queue();
				discovery.ifPresent((r, m) -> message.info(function.accept(r, m)));
			}
		} else {
			if (t.getClass().isAnnotationPresent(annotative)) {
				Mailer message = LabyrinthProvider.getService(Service.MESSENGER).getEmptyMailer();
				A e = t.getClass().getAnnotation(annotative);
				if (warning) {
					message.warn(function.accept(e, t.getClass().getMethods()[0])).queue();
				} else {
					message.info(function.accept(e, t.getClass().getMethods()[0])).queue();
				}
			}
		}
		return t;
	}


}
