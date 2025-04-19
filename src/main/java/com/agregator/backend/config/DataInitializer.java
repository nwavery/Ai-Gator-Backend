package com.agregator.backend.config;

import com.agregator.backend.model.Category;
import com.agregator.backend.model.Tool;
import com.agregator.backend.repository.CategoryRepository;
import com.agregator.backend.repository.ToolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final ToolRepository toolRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository, ToolRepository toolRepository) {
        this.categoryRepository = categoryRepository;
        this.toolRepository = toolRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedCategoriesIfNeeded();
        seedToolsIfNeeded();
    }

    private void seedCategoriesIfNeeded() {
        logger.info("Checking if initial category data needs to be seeded...");
        if (categoryRepository.count() == 0) {
            logger.info("No categories found. Seeding initial data...");
            List<String> categoryNames = Arrays.asList(
                "AI Detection", "Avatar", "Chat", "Copywriting", "Customer Support",
                "Design", "Developer Tools", "E-commerce", "Education", "Email Assistant",
                "Finance", "Fitness", "Fun Tools", "Gaming", "General Writing",
                "Gift Ideas", "Human Resources", "Image Editing", "Image Generation", "Legal",
                "Logo Generation", "Low Code/No Code", "Memory", "Music", "Presentations",
                "Productivity", "Prompts", "Real Estate", "Religion", "Research",
                "Sales", "SEO", "Social Media Assistant", "Spreadsheets", "SQL",
                "Startup Tools", "Story Teller", "Summarizer", "Text To Speech", "Transcriber",
                "Translation", "Video Editing", "Video Generation"
            );
            List<Category> categoriesToSave = categoryNames.stream().map(Category::new).toList();
            categoryRepository.saveAll(categoriesToSave);
            logger.info("Successfully seeded {} categories.", categoriesToSave.size());
        } else {
            logger.info("Categories already exist. Skipping category seeding.");
        }
    }

    private void seedToolsIfNeeded() {
        logger.info("Checking if tool data needs to be seeded...");
        if (toolRepository.count() == 0) {
            logger.info("No tools found. Seeding initial tool data from futuretools.io...");

            Map<String, Category> categories = fetchCategoriesByName(Arrays.asList(
                "Productivity", "Design", "Developer Tools", "Video Generation", "Research", "AI Detection", "Social Media Assistant"
            ));

            List<Tool> toolsToSave = new ArrayList<>();
            java.util.Random random = new java.util.Random();

            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Inabit.ai", "Creation of professional presentations", "https://inabit.ai", "Paid", List.of("presentation", "visualization"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Design", "Vectr", "Free vector graphics editor", "https://vectr.com/", "Free", List.of("vector", "design"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Developer Tools", "Baloon.dev", "AI development tool for coding tasks", "https://baloon.dev", "Paid", List.of("security", "quality"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Foundor.ai", "Create business plans interactively", "https://foundor.ai", "Paid", List.of("business plan", "startup"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "ExcelMatic", "Excel assistant using plain English", "https://excelmatic.com/", "Freemium", List.of("excel", "spreadsheet"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Design", "PXZ.AI", "Transforms inputs into visual content", "https://pxz.ai", "Paid", List.of("visuals", "content creation"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Video Generation", "DreamActor-M1", "Animate static portraits", "https://example.com/dreamactor", "Free", List.of("animation", "avatar"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Video Generation", "Hera", "Create motion graphics from text", "https://example.com/hera", "Paid", List.of("motion graphics", "video"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Design", "Artisk AI", "Create logos and brand designs", "https://artisk.ai/", "Freemium", List.of("logo", "branding"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Developer Tools", "Metatable.ai", "Software dev from code gen to deployment", "https://metatable.ai", "Paid", List.of("code generation", "deployment"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Docci.ai", "Extract structured data from documents", "https://docci.ai", "Paid", List.of("data extraction", "ocr"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Research", "Andi", "Privacy-focused AI search assistant", "https://andisearch.com/", "Free", List.of("search", "privacy"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Karax.ai", "AI meeting assistant transcriptions", "https://karax.ai", "Paid", List.of("meetings", "transcription"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Developer Tools", "Sleek", "Generate landing page code from prompts", "https://sleek.page/", "Paid", List.of("landing page", "code generation"), random);
            addToolIfCategoryExists(toolsToSave, categories, "AI Detection", "Polygraf AI", "Detect AI-generated text", "https://polygraf.ai/", "Freemium", List.of("ai writing", "detection"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Research", "Databar.ai", "No-code data collection & analysis", "https://databar.ai", "Freemium", List.of("no-code", "data enrichment"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Luna", "Project management with Jira insights", "https://luna.app/", "Paid", List.of("jira", "reporting"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Design", "Flora AI", "Integrate multiple AI models (images, etc)", "https://flora.ai/", "Paid", List.of("image generation", "video generation"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Video Generation", "Higgsfield", "Create lifelike human videos", "https://higgsfield.ai/", "Paid", List.of("avatar", "human video"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "ChatSlide AI", "Create presentations, videos, posts", "https://chatslide.ai/", "Freemium", List.of("presentation", "social media"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "Legion AI", "Natural language to SQL queries", "https://legion.ai/", "Freemium", List.of("sql", "analytics", "no-code"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Social Media Assistant", "Virlo", "Optimize short-form content for viral potential", "https://virlo.ai/", "Paid", List.of("tiktok", "instagram", "trends"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "WebCrawler API", "Extract data from websites at scale", "https://webcrawlerapi.com/", "Paid", List.of("web scraping", "data extraction"), random);
            addToolIfCategoryExists(toolsToSave, categories, "Productivity", "ToDiagram", "Convert data into interactive visual diagrams", "https://todiagram.com/", "Freemium", List.of("diagrams", "visualization"), random);

            if (!toolsToSave.isEmpty()) {
                toolRepository.saveAll(toolsToSave);
                logger.info("Successfully seeded {} tools.", toolsToSave.size());
            }
        } else {
            logger.info("Tool data already exists. Skipping tool seeding.");
        }
    }

    private Map<String, Category> fetchCategoriesByName(List<String> names) {
        return categoryRepository.findByNameInIgnoreCase(names).stream()
                   .collect(Collectors.toMap(cat -> cat.getName().toLowerCase(), cat -> cat));
    }

    private void addToolIfCategoryExists(List<Tool> tools, Map<String, Category> categoryMap, String categoryName, 
                                       String name, String description, String url, String pricing, 
                                       List<String> tags, java.util.Random random) {
        Category category = categoryMap.get(categoryName.toLowerCase());
        if (category != null) {
            Tool tool = new Tool(
                UUID.randomUUID().toString(),
                name,
                description,
                url,
                null,
                category,
                pricing,
                tags,
                "https://placehold.co/300x200?text=" + name.replaceAll(" ", "+"),
                5 + random.nextInt(200),
                new Date()
            );
            tools.add(tool);
        } else {
            logger.warn("Category '{}' not found for tool '{}'. Skipping tool seeding.", categoryName, name);
        }
    }
} 