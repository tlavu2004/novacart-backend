package com.tlavu.novacart.modules.catalog.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.tlavu.novacart.modules.catalog",
        importOptions = ImportOption.DoNotIncludeTests.class
)
@SuppressWarnings("unused") // ArchUnit discovers @ArchTest fields via reflection.
class CatalogArchitectureTest {

    private static final String DOMAIN = "..modules.catalog..domain..";
    private static final String APPLICATION = "..modules.catalog..application..";
    private static final String PRESENTATION = "..modules.catalog..presentation..";
    private static final String INFRASTRUCTURE = "..modules.catalog..infrastructure..";

    @ArchTest
    static final ArchRule domainDoesNotDependOnFrameworkOrOuterLayers = noClasses()
            .that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    APPLICATION,
                    PRESENTATION,
                    INFRASTRUCTURE,
                    "org.springframework..",
                    "jakarta.persistence..",
                    "org.hibernate.."
            );

    @ArchTest
    static final ArchRule applicationDoesNotDependOnInfrastructureOrPresentation = noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat()
            .resideInAnyPackage(INFRASTRUCTURE, PRESENTATION);

    @ArchTest
    static final ArchRule presentationDoesNotDependOnInfrastructure = noClasses()
            .that().resideInAPackage(PRESENTATION)
            .should().dependOnClassesThat()
            .resideInAPackage(INFRASTRUCTURE);

    @ArchTest
    static final ArchRule categoryApplicationDoesNotDependOnProduct = noClasses()
            .that().resideInAPackage("..modules.catalog.category.application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..modules.catalog.product..");
}
