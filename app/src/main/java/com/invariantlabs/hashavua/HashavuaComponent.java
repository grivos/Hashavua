package com.invariantlabs.hashavua;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = { HashavuaAppModule.class })
public interface HashavuaComponent extends HashavuaGraph {
    /**
     * An initializer that creates the graph from an application.
     */
    final static class Initializer {
        static HashavuaGraph init(HashavuaApp app) {
            return DaggerHashavuaComponent.builder()
                    .hashavuaAppModule(new HashavuaAppModule(app))
                    .build();
        }
        private Initializer() {} // No instances.
    }
}
