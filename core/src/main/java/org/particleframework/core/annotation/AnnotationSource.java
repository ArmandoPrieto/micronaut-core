/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.stream.Stream;

/**
 * <p>
 * An abstract interface around an source of {@link Annotation} instances. Since bean definition instances
 * can read annotations from many potential sources (factory beans, factory methods, method level, class level etc.). This
 * abstraction allows a simplified view for annotation discovery.
 * </p>
 *
 * <p>Generally annotation sources are prioritized in the following order:</p>
 *
 * <ol>
 *     <li>Method Level</li>
 *     <li>Class Level</li>
 *     <li>Factory Method</li>
 *     <li>Factory Class</li>
 * </ol>
 *
 * <p>The {@link #findAnnotation(Class)} method will return this first discovered annotation whilst traversing the above list.</p>
 *
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface AnnotationSource extends AnnotatedElement {


    /**
     * <p>The annotated elements that this {@link AnnotationSource} is able to resolve annotations from</p>
     *
     * <p>These elements are used when resolving annotations via the {@link #findAnnotationsWithStereoType(Class)} method</p>
     *
     * @return An array of {@link AnnotatedElement} instances
     */
    AnnotatedElement[] getAnnotatedElements();


    /**
     * Find an annotation by type from the {@link #getAnnotatedElements()} of this class
     *
     * @param type The type
     * @param <A> The generic type
     * @return An {@link Optional} of the type
     */
    default <A extends Annotation> Optional<A> findAnnotation(Class<A> type) {
        AnnotatedElement[] elements = getAnnotatedElements();
        for (AnnotatedElement element : elements) {
            Optional<A> optional = AnnotationUtil.findAnnotation(element, type);
            if(optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    /**
     * Find the first annotation for the given stereotype on the method
     *
     * @param stereotype The method
     * @return The stereotype
     */
    default Optional<Annotation> findAnnotationWithStereoType(Class<? extends Annotation> stereotype) {
        AnnotatedElement[] candidates = getAnnotatedElements();
        for (AnnotatedElement candidate : candidates) {
            Optional<? extends Annotation> opt = AnnotationUtil.findAnnotationWithStereoType(candidate, stereotype);
            if(opt.isPresent()) {
                return (Optional<Annotation>) opt;
            }
        }
        return Optional.empty();
    }

    /**
     * Find all the annotations for the given stereotype on the method
     * @param stereotype The method
     * @return The stereotype
     */
    default Set<Annotation> findAnnotationsWithStereoType(Class<?> stereotype) {
        AnnotatedElement[] candidates = getAnnotatedElements();
        return AnnotationUtil.findAnnotationsWithStereoType(candidates, stereotype);
    }


    /**
     * Get a concrete annotation for the given annotation type searching all of the {@link #getAnnotatedElements()}
     *
     * @param annotationClass The annotation class
     * @param <T> The annotation generic type
     * @return The annotation or null if it doesn't exist
     * @see #findAnnotation(Class)
     */
    @Override
    default <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (AnnotatedElement annotatedElement : getAnnotatedElements()) {
            T annotation = annotatedElement.getAnnotation(annotationClass);
            if(annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * @return A merged view of all of the annotations from {@link  #getAnnotatedElements()}
     */
    @Override
    default Annotation[] getAnnotations() {
        AnnotatedElement[] elements = getAnnotatedElements();
        return Arrays.stream(elements)
              .flatMap(element -> Arrays.stream(element.getAnnotations()))
              .toArray(Annotation[]::new);
    }

    /**
     * @return A merged view of all of the declared annotations from {@link  #getAnnotatedElements()}
     */
    @Override
    default Annotation[] getDeclaredAnnotations() {
        AnnotatedElement[] elements = getAnnotatedElements();
        return Arrays.stream(elements)
                .flatMap(element -> Arrays.stream(element.getDeclaredAnnotations()))
                .toArray(Annotation[]::new);
    }

    /**
     * Return whether an annotation is present for any of the given stereotypes
     *
     * @param stereotypes The stereotypes
     * @return True if it is
     */
    default boolean isAnnotationStereotypePresent(String... stereotypes) {
        for (AnnotatedElement annotatedElement : getAnnotatedElements()) {
            for (String stereotype : stereotypes) {
                if( AnnotationUtil.findAnnotationWithStereoType(annotatedElement, stereotype).isPresent() ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether any annotation is present for any of the given annotation names
     *
     * @param annotationNames The annotation names
     * @return True if it is
     */
    default boolean isAnyAnnotationPresent(String...annotationNames) {
        Annotation[] annotations = getAnnotations();
        for (String annotationName : annotationNames) {
            if( Arrays.stream(annotations).anyMatch(ann -> ann.annotationType().getName().equals(annotationName)) ) {
                return true;
            }
        }
        return false;
    }
}
