package fr.esgi.pa.server.language.core;

public enum LanguageName {
    C("C"),
    CPP("C++"),
    JAVA("Java"),
    TYPESCRIPT("TypeScript"),
    RUST("Rust");

    public final String name;
    LanguageName(String name) {
        this.name = name;
    }
}
