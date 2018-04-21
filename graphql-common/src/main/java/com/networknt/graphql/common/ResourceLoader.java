package com.networknt.graphql.common;

import com.networknt.resources.PathResourceProvider;
import com.networknt.resources.PredicatedHandlersProvider;
import com.networknt.service.SingletonServiceFactory;

/**
 * @author Nicholas Azar
 * Created on April 21, 2018
 */
public class ResourceLoader {

    public static PathResourceProvider[] pathResourceProviders = SingletonServiceFactory.getBeans(PathResourceProvider.class);
    public static PredicatedHandlersProvider[] predicatedHandlersProviders = SingletonServiceFactory.getBeans(PredicatedHandlersProvider.class);
}
