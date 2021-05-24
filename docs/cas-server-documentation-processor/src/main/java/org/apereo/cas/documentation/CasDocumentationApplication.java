package org.apereo.cas.documentation;

import org.apereo.cas.metadata.CasConfigurationMetadataCatalog;
import org.apereo.cas.metadata.CasReferenceProperty;
import org.apereo.cas.metadata.ConfigurationMetadataCatalogQuery;
import org.apereo.cas.services.RegisteredServiceProperty;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is {@link CasDocumentationApplication}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
public class CasDocumentationApplication {
    public static void main(final String[] args) {
        var results = CasConfigurationMetadataCatalog.query(
            ConfigurationMetadataCatalogQuery.builder()
                .queryType(ConfigurationMetadataCatalogQuery.QueryTypes.CAS)
                .build());

        var groups = new HashMap<String, Set<CasReferenceProperty>>();
        results.properties()
            .stream()
            .filter(property -> StringUtils.isNotBlank(property.getModule()))
            .forEach(property -> {
                if (groups.containsKey(property.getModule())) {
                    groups.get(property.getModule()).add(property);
                } else {
                    var values = new TreeSet<CasReferenceProperty>();
                    values.add(property);
                    groups.put(property.getModule(), values);
                }
            });
        var dataDirectory = args[0];
        var projectVersion = args[1];
        var projectRootDirectory = args[2];

        var dataPath = new File(dataDirectory, projectVersion);
        if (dataPath.exists()) {
            FileUtils.deleteQuietly(dataPath);
        }
        dataPath.mkdirs();
        groups.forEach((key, value) -> {
            var destination = new File(dataPath, key);
            destination.mkdirs();
            var configFile = new File(destination, "config.yml");
            CasConfigurationMetadataCatalog.export(configFile, value);
        });

        exportThirdPartyConfiguration(dataPath);
        exportRegisteredServiceProperties(dataPath);
        exportTemplateViews(projectRootDirectory, dataPath);
    }

    private static void exportTemplateViews(final String projectRootDirectory, final File dataPath) {
        var serviceProps = new File(dataPath, "userinterface-templates");
        if (serviceProps.exists()) {
            FileUtils.deleteQuietly(serviceProps);
        }
        serviceProps.mkdirs();
        var uiFile = new File(serviceProps, "config.yml");
        var properties = new ArrayList<Map<?, ?>>();

        var root = new File(projectRootDirectory, "support/cas-server-support-thymeleaf");
        var parent = new File(root, "src/main/resources/templates");

        var files = FileUtils.listFiles(parent, new String[]{"html"}, true);
        files
            .stream()
            .sorted()
            .forEach(file -> {
                var map = new LinkedHashMap<String, Object>();
                var path = StringUtils.remove(file.getAbsolutePath(), root.getAbsolutePath());
                map.put("name", path);
                properties.add(map);
            });
        CasConfigurationMetadataCatalog.export(uiFile, properties);
    }

    private static void exportRegisteredServiceProperties(final File dataPath) {
        var serviceProps = new File(dataPath, "registered-service-properties");
        if (serviceProps.exists()) {
            FileUtils.deleteQuietly(serviceProps);
        }
        serviceProps.mkdirs();
        var servicePropsFile = new File(serviceProps, "config.yml");
        var properties = new ArrayList<Map<?, ?>>();
        for (var property : RegisteredServiceProperty.RegisteredServiceProperties.values()) {
            var map = new LinkedHashMap<String, Object>();
            map.put("name", property.getPropertyName());
            map.put("defaultValue", property.getDefaultValue());
            map.put("type", property.getType().name());
            map.put("group", property.getGroup().name());
            properties.add(map);
        }
        CasConfigurationMetadataCatalog.export(servicePropsFile, properties);
    }

    private static void exportThirdPartyConfiguration(final File dataPath) {
        var results = CasConfigurationMetadataCatalog.query(
            ConfigurationMetadataCatalogQuery.builder()
                .queryType(ConfigurationMetadataCatalogQuery.QueryTypes.THIRD_PARTY)
                .build());
        var destination = new File(dataPath, "third-party");
        if (destination.exists()) {
            FileUtils.deleteQuietly(destination);
        }
        destination.mkdirs();
        var configFile = new File(destination, "config.yml");
        CasConfigurationMetadataCatalog.export(configFile, results.properties());
    }
}
