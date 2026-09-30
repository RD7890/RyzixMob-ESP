package com.ryzix.mobesp;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class EntityESPScreen extends Screen {
    private TextFieldWidget searchField;
    private final List<EntityType<?>> allMobs = new ArrayList<>();
    private final List<EntityType<?>> filteredMobs = new ArrayList<>();
    
    private int scrollOffset = 0;
    private static final int ITEM_HEIGHT = 24;

    public EntityESPScreen() {
        super(Text.literal("Entity ESP Menu"));
        for (EntityType<?> type : Registries.ENTITY_TYPE) {
            allMobs.add(type);
        }
        updateSearch("");
    }

    @Override
    protected void init() {
        searchField = new TextFieldWidget(textRenderer, width / 2 - 100, 20, 200, 20, Text.literal("Search Mobs"));
        searchField.setChangedListener(this::updateSearch);
        addSelectableChild(searchField);
    }

    private void updateSearch(String query) {
        filteredMobs.clear();
        String q = query.toLowerCase();
        for (EntityType<?> type : allMobs) {
            String name = type.getName().getString().toLowerCase();
            if (name.contains(q)) {
                filteredMobs.add(type);
            }
        }
        scrollOffset = 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 5, 0xFFFFFF);
        searchField.render(context, mouseX, mouseY, delta);
        
        int startY = 50;
        int maxVisible = (height - startY - 20) / ITEM_HEIGHT;
        
        int startIndex = scrollOffset / ITEM_HEIGHT;
        
        for (int i = 0; i < maxVisible && (startIndex + i) < filteredMobs.size(); i++) {
            int index = startIndex + i;
            EntityType<?> type = filteredMobs.get(index);
            
            int itemY = startY + (i * ITEM_HEIGHT);
            boolean active = EntityESP.activeMobs.contains(type);
            
            int color = active ? 0x00FF00 : 0xFF0000;
            String text = type.getName().getString() + " [" + (active ? "ON" : "OFF") + "]";
            
            boolean hovered = mouseX >= width / 2 - 100 && mouseX <= width / 2 + 100 && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;
            if (hovered) {
                context.fill(width / 2 - 102, itemY - 2, width / 2 + 102, itemY + ITEM_HEIGHT - 2, 0x44FFFFFF);
            }
            
            context.drawCenteredTextWithShadow(textRenderer, text, width / 2, itemY + 4, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        int startY = 50;
        int maxVisible = (height - startY - 20) / ITEM_HEIGHT;
        int startIndex = scrollOffset / ITEM_HEIGHT;
        
        for (int i = 0; i < maxVisible && (startIndex + i) < filteredMobs.size(); i++) {
            int index = startIndex + i;
            int itemY = startY + (i * ITEM_HEIGHT);
            
            if (mouseX >= width / 2 - 100 && mouseX <= width / 2 + 100 && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT) {
                EntityType<?> type = filteredMobs.get(index);
                if (EntityESP.activeMobs.contains(type)) {
                    EntityESP.activeMobs.remove(type);
                } else {
                    EntityESP.activeMobs.add(type);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double amount) {
        scrollOffset -= amount * ITEM_HEIGHT;
        if (scrollOffset < 0) scrollOffset = 0;
        int maxScroll = Math.max(0, (filteredMobs.size() * ITEM_HEIGHT) - (height - 70));
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
        return true;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchField.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
