// SmartFitGUI.java
// ─────────────────────────────────────────────────────────────────────────────
// All JavaFX GUI code for SmartFit.
// Receives model objects from Main.java via setModel().
// No logic changes from App.java — only restructured into a separate class.
//
// Screens:
//   1. Home              – gradient bg, SmartFit title, three buttons
//   2. Add Item          – photo upload + fields per category → saves to Wardrobe
//   3. Wardrobe / Build  – item grid (left) + live preview panel (right)
//   4. Outerwear & Acc   – flat-lay full outfit view + selection grid
//   5. Try Combinations  – THE FIT card + suggestions + rating (unused screen kept)
//   6. Outfit Scheduler  – month calendar, click day to assign outfit
//   7. Online Search     – drag & drop image upload (API connects here)
// ─────────────────────────────────────────────────────────────────────────────

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;



public class SmartFitGUI extends Application {

    // ── Background image path ─────────────────────────────────────────────
    private static final String BG_IMAGE_PATH =
        "C:\\Users\\hp\\Desktop\\javGUI\\SmartFit\\bg.png";

    // ── Colour palette ────────────────────────────────────────────────────
    private static final Color  C_PINK    = Color.web("#F4A7B9");
    private static final Color  C_MID     = Color.web("#C084FC");
    private static final Color  C_PURPLE  = Color.web("#7C3AED");
    private static final String BTN_FILL      = "#E06FA0";
    private static final String BTN_HOV       = "#C8527E";
    private static final String BTN_CORAL     = "#F4845F";
    private static final String BTN_LIME      = "#CCFF00";
    private static final String BTN_LIME_TXT  = "#3A5200";
    private static final String CARD_BG   = "rgba(140,80,200,0.48)";
    private static final String CARD_HOV  = "rgba(170,100,230,0.65)";
    private static final String CARD_SEL  = "rgba(255,255,255,0.30)";
    private static final String TXT_CREAM = "#FFF5E4";
    private static final String TXT_LIGHT = "rgba(255,255,255,0.85)";
    private static final String TXT_DIM   = "rgba(255,255,255,0.52)";
    private static final String STAR_ON   = "#FFD700";
    private static final String STAR_OFF  = "rgba(255,255,255,0.30)";

    // ── Model objects — set by Main.java before launch ────────────────────
    // static so setModel() can write them before start() is called
    private static Wardrobe        _wardrobe;
    private static OutfitManager   _outfitMgr;
    private static OutfitScheduler _scheduler;

    // Instance references used throughout the GUI methods
    private Wardrobe        wardrobe;
    private OutfitManager   outfitMgr;
    private OutfitScheduler scheduler;

    /**
     * Called by Main.java before Application.launch().
     * Stores the model objects so start() can pick them up.
     */
    public static void setModel(Wardrobe w, OutfitManager om, OutfitScheduler os) {
        _wardrobe  = w;
        _outfitMgr = om;
        _scheduler = os;
    }

    // ── Current outfit-builder selection ──────────────────────────────────
    private top         selTop   = null;
    private bottom      selBot   = null;
    private shoes       selShoe  = null;
    private outerwear   selOuter = null;
    private accessories selAcc   = null;

    // NOTE: imgMap removed — image path is now stored directly on each
    // ClothingItems object via item.getImagePath() / item.setImagePath()
    // This means it persists automatically with serialization.

    // Upload URLs for online search screen
    private String searchUrl = null;
    private String refTopUrl = null;
    private String refBotUrl = null;

    // Root pane — all screens swap inside this
    private StackPane root;

    // Live grid rows rebuilt when wardrobe changes
    private HBox rowTops, rowBots, rowShoes, rowOuter, rowAcc;

    // Live preview panel (right side of wardrobe screen)
    private VBox previewPanel;

    // Calendar month shown in scheduler
    private YearMonth calMonth = YearMonth.now();

    // ═════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {
        // Pick up the model objects that Main.java set before launching
        wardrobe  = _wardrobe;
        outfitMgr = _outfitMgr;
        scheduler = _scheduler;

        root = new StackPane();
        showHome();

        stage.setScene(new Scene(root, 1120, 700));
        stage.setTitle("SmartFit");
        stage.setMinWidth(940);
        stage.setMinHeight(600);

        // Save everything when the window is closed
        stage.setOnCloseRequest(e ->
            FileManager.saveAll(wardrobe, outfitMgr, scheduler)
        );

        stage.show();
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 1 — HOME
    // ═════════════════════════════════════════════════════════════════════
    private void showHome() {
        StackPane bg = grad();

        Label title = new Label("SmartFit");
        title.setStyle("-fx-font-size:74px;-fx-font-weight:bold;-fx-font-family:'Georgia';"
                + "-fx-font-style:italic;-fx-text-fill:" + TXT_CREAM + ";"
                + "-fx-effect:dropshadow(gaussian,rgba(60,0,100,0.55),20,0,2,5);");

        Label tagline = new Label("Your intelligent wardrobe, styled for you.");
        tagline.setStyle("-fx-font-size:14px;-fx-text-fill:" + TXT_LIGHT + ";");

        Button btnSearch   = pill("Search Outfits Online", 270, 58);
        Button btnWardrobe = pill("Wardrobe Manager",      250, 58);
        Button btnSchedule = pill("Outfit Schedule",       230, 58);

        btnSearch  .setOnAction(e -> fade(this::showOnlineSearch));
        btnWardrobe.setOnAction(e -> fade(this::showAddItem));
        btnSchedule.setOnAction(e -> fade(this::showScheduler));

        HBox row = new HBox(28, btnSearch, btnWardrobe, btnSchedule);
        row.setAlignment(Pos.CENTER);

        VBox body = new VBox(26, title, tagline, row);
        body.setAlignment(Pos.CENTER);
        bg.getChildren().add(body);
        show(bg);
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 2 — ADD CLOTHING ITEM
    // ═════════════════════════════════════════════════════════════════════
    private void showAddItem() {
        StackPane bg = grad();

        Label title = screenTitle("Add Clothing Item");
        Label sub   = dim("Upload a photo and fill in the details, then add to your wardrobe.");

        // Category toggle tabs
        ToggleGroup catGrp = new ToggleGroup();
        String[] catNames  = {"👕  Top","👖  Bottom","👟  Shoes","🧥  Outerwear","👜  Accessories"};
        HBox tabRow = new HBox(8);
        tabRow.setAlignment(Pos.CENTER);
        for (String c : catNames) {
            ToggleButton tb = new ToggleButton(c);
            tb.setToggleGroup(catGrp);
            styleTog(tb, false);
            tb.selectedProperty().addListener((o, old, v) -> styleTog(tb, v));
            tabRow.getChildren().add(tb);
        }
        ((ToggleButton) tabRow.getChildren().get(0)).setSelected(true);

        // Photo upload zone
        ImageView photoIV = new ImageView();
        photoIV.setFitWidth(210); photoIV.setFitHeight(210);
        photoIV.setPreserveRatio(true); photoIV.setVisible(false);

        Label photoHint = new Label("📷  Click or drag photo\n(transparent PNG best)");
        photoHint.setStyle("-fx-font-size:13px;-fx-text-fill:" + TXT_LIGHT
                + ";-fx-text-alignment:center;");
        photoHint.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        photoHint.setWrapText(true);

        StackPane photoZone = dropCard(photoHint, photoIV, 250, 250);
        final String[] photoURL = {null};
        photoZone.setOnMouseClicked(e -> {
            File f = pickImg(); if (f == null) return;
            photoURL[0] = f.toURI().toString();
            showInDrop(photoIV, photoHint, photoURL[0]);
        });
        dnd(photoZone, photoIV, photoHint, u -> photoURL[0] = u);

        Label tip = dim("💡 Transparent PNG gives the cleanest look in the wardrobe.");
        tip.setWrapText(true); tip.setMaxWidth(250);

        VBox photoCol = new VBox(10, photoZone, tip);
        photoCol.setAlignment(Pos.TOP_CENTER); photoCol.setMaxWidth(260);

        // Fields — all optional, defaults shown in prompt text
        TextField brandF  = field("Brand  (default: Unknown)",    185);
        TextField sizeF   = field("Size   (default: One Size)",   135);
        TextField colourF = field("Colour (default: White)",      165);
        TextField priceF  = field("Price PKR (default: 0)",       120);
        TextField fabricF = field("Fabric (default: Unknown)",    165);
        ComboBox<String> seasonCB = cb("All Season","Summer","Winter","Spring","Autumn");

        TextField styleF  = field("Style  (default: Casual)",     165);
        ComboBox<String> sleeveCB = cb("Long Sleeve","Short Sleeve","Sleeveless","Half Sleeve");
        TextField heelF   = field("Heel cm (default: 0 = flat)",  145);
        CheckBox openCB   = new CheckBox("Open toe");
        openCB.setStyle("-fx-text-fill:white;-fx-font-size:12px;");

        VBox extraBox = new VBox(10);
        extraBox.setAlignment(Pos.CENTER_LEFT);
        extraBox.getChildren().add(row2(node("Style", styleF), node("Sleeve", sleeveCB)));

        catGrp.selectedToggleProperty().addListener((o, old, sel) -> {
            extraBox.getChildren().clear();
            if (sel == null) return;
            String cat = stripEmoji(((ToggleButton) sel).getText());
            HBox er = new HBox(12, node("Style", styleF));
            er.setAlignment(Pos.CENTER_LEFT);
            if (cat.equals("Top"))   er.getChildren().add(node("Sleeve", sleeveCB));
            if (cat.equals("Shoes")) {
                er.getChildren().add(node("Heel cm", heelF));
                er.getChildren().add(openCB);
            }
            extraBox.getChildren().add(er);
        });

        GridPane grd = new GridPane();
        grd.setHgap(14); grd.setVgap(12);
        grd.add(node("Brand",      brandF),  0,0); grd.add(node("Size",   sizeF),   1,0);
        grd.add(node("Colour",     colourF), 0,1); grd.add(node("Price",  priceF),  1,1);
        grd.add(node("Fabric",     fabricF), 0,2); grd.add(node("Season", seasonCB),1,2);

        Label msgLbl = new Label("");
        msgLbl.setStyle("-fx-font-size:12px;-fx-text-fill:#FFD700;");

        Button addBtn = pill("➕  Add to Wardrobe", 220, 48);
        Button goBtn  = semiBtn("View My Wardrobe →");
        goBtn.setOnAction(e -> { refreshGrid(); fade(this::showWardrobe); });

        addBtn.setOnAction(ev -> {
            int price = 0;
            try { price = Integer.parseInt(priceF.getText().trim()); }
            catch (Exception ex) { price = 0; }

            String cat   = stripEmoji(((ToggleButton) catGrp.getSelectedToggle()).getText());
            String brand = brandF.getText().isBlank()  ? "Unknown"  : brandF.getText().trim();
            String size  = sizeF.getText().isBlank()   ? "One Size" : sizeF.getText().trim();
            String col   = colourF.getText().isBlank() ? "White"    : colourF.getText().trim();
            String fab   = fabricF.getText().isBlank() ? "Unknown"  : fabricF.getText().trim();
            String sea   = seasonCB.getValue();
            String sty   = styleF.getText().isBlank()  ? "Casual"   : styleF.getText().trim();
            String imagePath = null;

            ClothingItems item = switch (cat) {
                case "Top"       -> new top(size,brand,price,fab,col,0,sea,imagePath,sty,sleeveCB.getValue());
                case "Bottom"    -> new bottom(size,brand,price,fab,col,0,sea,imagePath,sty);
                case "Shoes"     -> {
                    int h = 0;
                    try { h = Integer.parseInt(heelF.getText().trim()); } catch (Exception ig) {}
                    yield new shoes(size,brand,price,fab,col,0,sea,imagePath,sty,h,openCB.isSelected());
                }
                case "Outerwear" -> new outerwear(size,brand,price,fab,col,0,sea,imagePath,sty);
                default          -> new accessories(size,brand,price,fab,col,0,sea,imagePath,sty);
            };

            if (photoURL[0] != null){
            try{
            // Convert JavaFX URL to real file path
            String sourcePath = new File(
            new java.net.URI(photoURL[0])
            ).getAbsolutePath();

            // Copy image into images/category/ folder permanently
            String savedPath = PngFiles.saveImage(sourcePath, cat);

            if(savedPath != null){
                // Store path DIRECTLY on the item — persists with serialization
                item.setImagePath(new File(savedPath).toURI().toString());
            } else {
                // Copy failed — store original path as fallback
                item.setImagePath(photoURL[0]);
            }

            } catch (Exception ex) {
            System.out.println("Image path error: " + ex.getMessage());
            item.setImagePath(photoURL[0]);
            }
        }
            wardrobe.addItems(item);

            // Save after every add so data is never lost
            FileManager.saveAll(wardrobe, outfitMgr, scheduler);

            brandF.clear(); sizeF.clear(); colourF.clear();
            priceF.clear(); fabricF.clear(); heelF.clear();
            photoIV.setVisible(false); photoHint.setVisible(true); photoURL[0] = null;
            msgLbl.setText("✅  " + cat + " added: " + brand + " (" + col + ")");
        });

        VBox fieldsCol = new VBox(14, grd, extraBox, msgLbl, addBtn, goBtn);
        fieldsCol.setAlignment(Pos.CENTER_LEFT); fieldsCol.setMaxWidth(430);

        HBox mainRow = new HBox(28, photoCol, fieldsCol);
        mainRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, title, sub, tabRow, mainRow);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20,40,30,40));

        Button backBtn = backBtn();
        backBtn.setOnAction(e -> fade(this::showHome));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(12,0,0,12));

        bg.getChildren().addAll(scrollWrap(content), backBtn);
        show(bg);
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 3 — WARDROBE / OUTFIT BUILDER
    // Left: scrollable item grid    Right: live outfit preview
    // ═════════════════════════════════════════════════════════════════════
    private void showWardrobe() {
        StackPane bg = grad();

        Label title = screenTitle("My Wardrobe");
        TextField nameF = field("Outfit name", 180);

        final int[] starVal = {3};
        HBox starPicker = starRow(3, true);
        for (int i = 0; i < 5; i++) {
            final int s = i + 1;
            starPicker.getChildren().get(i).setOnMouseClicked(e -> {
                starVal[0] = s; updateStars(starPicker, s);
            });
        }

        // Lime Save Outfit button
        Button saveBtn = new Button("Save Outfit");
        saveBtn.setStyle("-fx-background-color:" + BTN_LIME + ";-fx-text-fill:" + BTN_LIME_TXT + ";"
                + "-fx-font-size:14px;-fx-font-weight:bold;"
                + "-fx-background-radius:26;-fx-padding:10 26;-fx-cursor:hand;");

        Button outerAccBtn = pill("Outerwear & Accessories +", 260, 44);
        outerAccBtn.setOnAction(e -> fade(this::showOuterAcc));

        Button addMoreBtn = semiBtn("+ Add more items");
        addMoreBtn.setOnAction(e -> fade(this::showAddItem));

        saveBtn.setOnAction(e -> {
            if (selTop == null || selBot == null || selShoe == null) {
                alert("Select at least a Top, Bottom and Shoes."); return;
            }
            String name = nameF.getText().isBlank()
                    ? "Outfit " + (outfitMgr.getTotalOutfits() + 1)
                    : nameF.getText().trim();
            Outfit o = new Outfit(name, selTop, selBot, selShoe, selAcc, selOuter);
            o.setRating(starVal[0]);
            outfitMgr.saveOutfit(o);
            selTop.incrementWear(); selBot.incrementWear(); selShoe.incrementWear();
            if (selAcc   != null) selAcc.incrementWear();
            if (selOuter != null) selOuter.incrementWear();
            selTop = null; selBot = null; selShoe = null; selAcc = null; selOuter = null;
            nameF.clear();
            refreshGrid();
            refreshPreview();
            // Save after outfit is saved
            FileManager.saveAll(wardrobe, outfitMgr, scheduler);
            alert("✅  Outfit '" + name + "' saved!");
        });

        Button aiPopupBtn = new Button("✨ AI Stylist");
        aiPopupBtn.setStyle("-fx-background-color:" + BTN_LIME + ";-fx-text-fill:" + BTN_LIME_TXT + ";"
        + "-fx-font-size:13px;-fx-font-weight:bold;"
        + "-fx-background-radius:26;-fx-padding:8 20;-fx-cursor:hand;");
        aiPopupBtn.setOnAction(e -> showAIPopup());

        HBox topBar = new HBox(12, nameF, starPicker, saveBtn, outerAccBtn, addMoreBtn, aiPopupBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0,0,6,0));

        Label hint = dim("📌  Click one item per row to select — watch the preview update on the right");

        rowTops = hrow(); rowBots = hrow(); rowShoes = hrow();
        refreshGrid();

        VBox grid = new VBox(10,
                gridRow("TOPS",    rowTops),
                gridRow("BOTTOMS", rowBots),
                gridRow("SHOES",   rowShoes)
        );

        VBox leftCol = new VBox(10, topBar, hint, grid);
        leftCol.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        previewPanel = buildPreviewPanel();

        HBox splitRow = new HBox(18, leftCol, previewPanel);
        splitRow.setAlignment(Pos.TOP_CENTER);

        VBox page = new VBox(14, title, splitRow);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(20, 24, 24, 24));

        Button backBtn = backBtn();
        backBtn.setOnAction(e -> fade(this::showHome));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(8,0,0,8));

        bg.getChildren().addAll(scrollWrap(page), backBtn);
        show(bg);
    }

    // ── Live preview panel ────────────────────────────────────────────────
    private VBox buildPreviewPanel() {
        VBox panel = new VBox(0);
        panel.setPrefWidth(280); panel.setMinWidth(260);
        panel.setPadding(new Insets(14, 10, 14, 10));
        panel.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:20;");
        panel.setAlignment(Pos.TOP_CENTER);
        refreshPreviewInto(panel);
        return panel;
    }

    private void refreshPreview() {
        if (previewPanel != null) refreshPreviewInto(previewPanel);
    }

    private void refreshPreviewInto(VBox panel) {
        panel.getChildren().clear();
        Label hdr = new Label("✦   THE FIT   ✦");
        hdr.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
        hdr.setPadding(new Insets(0, 0, 10, 0));
        panel.getChildren().add(hdr);
        // Wardrobe panel shows only the 3 core slots
        // Slot dimensions: Top 220×200, Bottom 220×200, Shoes 220×130
        panel.getChildren().add(previewSlot("👕  TOP",    selTop,  "top",    220, 200));
        panel.getChildren().add(previewSlot("👖  BOTTOM", selBot,  "bottom", 220, 200));
        panel.getChildren().add(previewSlot("👟  SHOES",  selShoe, "shoes",  220, 130));
    }

    private StackPane previewSlot(String label, ClothingItems item, String type,
                                   double slotW, double slotH) {
        StackPane slot = new StackPane();
        slot.setPrefSize(slotW, slotH);
        slot.setMaxSize(slotW, slotH);
        slot.setMinSize(slotW, slotH);

        if (item == null) {
            slot.setStyle("-fx-background-color:rgba(255,255,255,0.07);"
                    + "-fx-border-color:rgba(255,255,255,0.22);"
                    + "-fx-border-style:dashed;-fx-border-width:1.5;");
            Label ph = new Label(label);
            ph.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_DIM + ";-fx-font-weight:bold;");
            slot.getChildren().add(ph);
        } else {
            String url = item.getImagePath();
            if (url != null) {
                ImageView iv = new ImageView(new Image(url, true));
                iv.setFitWidth(slotW); iv.setFitHeight(slotH); iv.setPreserveRatio(true);
                slot.setStyle("-fx-background-color:transparent;");
                slot.getChildren().add(iv);
            } else {
                Rectangle swatch = new Rectangle(slotW, slotH);
                try { swatch.setFill(Color.web(colHex(item.getColour()))); }
                catch (Exception ex) { swatch.setFill(Color.LIGHTGRAY); }
                swatch.setOpacity(0.55);
                Label emoji = new Label(typeEmoji(type));
                emoji.setStyle("-fx-font-size:" + (int)(slotH * 0.35) + "px;");
                VBox info = new VBox(4);
                info.setAlignment(Pos.BOTTOM_CENTER);
                Label brandL = new Label(item.getBrand());
                brandL.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
                Label colL = new Label(item.getColour() + "  ·  " + item.getSize());
                colL.setStyle("-fx-font-size:10px;-fx-text-fill:" + TXT_LIGHT + ";");
                info.getChildren().addAll(brandL, colL);
                info.setPadding(new Insets(0, 0, 8, 0));
                slot.setStyle("-fx-background-color:rgba(0,0,0,0.12);");
                slot.getChildren().addAll(swatch, emoji, info);
                StackPane.setAlignment(info, Pos.BOTTOM_CENTER);
            }
        }
        return slot;
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 4 — OUTERWEAR & ACCESSORIES
    // Left: full outfit flat-lay    Right: selection grid + rating
    // ═════════════════════════════════════════════════════════════════════
    private void showOuterAcc() {
        StackPane bg = grad();

        Label title = screenTitle("Complete Your Outfit");

        // Flat-lay slots
        // Layout: [outerwear] [top] [accessory]
        //         [shoes]     [bottom]
        StackPane slotOuter = flatSlot(selOuter, "outer",  "🧥  OUTERWEAR", 200, 200);
        StackPane slotTop   = flatSlot(selTop,   "top",    "👕  TOP",        200, 200);
        StackPane slotAcc   = flatSlot(selAcc,   "acc",    "👜  ACCESSORY",  150, 150);
        StackPane slotBot   = flatSlot(selBot,   "bottom", "👖  BOTTOM",     200, 210);
        StackPane slotShoe  = flatSlot(selShoe,  "shoes",  "👟  SHOES",      200, 160);

        HBox row1 = new HBox(10, slotOuter, slotTop, slotAcc);
        row1.setAlignment(Pos.BOTTOM_LEFT);
        HBox row2 = new HBox(10, slotShoe, slotBot);
        row2.setAlignment(Pos.TOP_LEFT);

        VBox flatlay = new VBox(8, row1, row2);
        flatlay.setPadding(new Insets(16));
        flatlay.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:20;");
        flatlay.setAlignment(Pos.TOP_LEFT);

        // Right side — selection grid
        rowOuter = hrow(); rowAcc = hrow();
        refreshOuterAccGrid();
        VBox outerGrid = gridRow("OUTERWEAR",   rowOuter);
        VBox accGrid   = gridRow("ACCESSORIES", rowAcc);

        // Rating
        final int[] ratingVal = {0};
        Label ratingTitle = new Label("RATING:");
        ratingTitle.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
        HBox stars = starRow(0, true);
        Label ratingLbl = new Label("Click a star to rate");
        ratingLbl.setStyle("-fx-font-size:11px;-fx-text-fill:" + TXT_LIGHT + ";");
        for (int i = 0; i < 5; i++) {
            final int s = i + 1;
            stars.getChildren().get(i).setOnMouseClicked(e -> {
                ratingVal[0] = s;
                updateStars(stars, s);
                ratingLbl.setText(s + " / 5  " + ratingWord(s));
                ArrayList<Outfit> all = outfitMgr.getAllOutfits();
                if (!all.isEmpty()) all.get(all.size()-1).setRating(s);
            });
        }
        VBox ratingCard = new VBox(8, ratingTitle, stars, ratingLbl);
        ratingCard.setPadding(new Insets(12));
        ratingCard.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:12;");

        Button saveOutfitBtn = new Button("Save Outfit");
        saveOutfitBtn.setStyle("-fx-background-color:" + BTN_LIME + ";-fx-text-fill:" + BTN_LIME_TXT + ";"
                + "-fx-font-size:13px;-fx-font-weight:bold;"
                + "-fx-background-radius:24;-fx-padding:9 22;-fx-cursor:hand;");
        saveOutfitBtn.setOnAction(e -> {
            ArrayList<Outfit> all = outfitMgr.getAllOutfits();
            if (all.isEmpty()) { alert("Save your outfit first in My Wardrobe."); return; }
            all.get(all.size()-1).setRating(ratingVal[0]);
            FileManager.saveAll(wardrobe, outfitMgr, scheduler);
            alert("✅  Rating saved!");
        });

        Button doneBtn = pill("← Back to Wardrobe", 220, 46);
        doneBtn.setOnAction(e -> { refreshPreview(); fade(this::showWardrobe); });

        HBox actionRow = new HBox(12, saveOutfitBtn, doneBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox aiCollapsibleSection = createCollapsibleAISection();

        VBox rightCol = new VBox(12, outerGrid, accGrid, ratingCard, actionRow, aiCollapsibleSection);
        rightCol.setMaxWidth(480);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        HBox mainRow = new HBox(20, flatlay, rightCol);
        mainRow.setAlignment(Pos.TOP_CENTER);

        VBox page = new VBox(16, title, mainRow);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(18, 24, 28, 24));

        Button backBtn = backBtn();
        backBtn.setOnAction(e -> fade(this::showWardrobe));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10,0,0,10));

        bg.getChildren().addAll(scrollWrap(page), backBtn);
        show(bg);
    }

    // ── Flat-lay slot ─────────────────────────────────────────────────────
    private StackPane flatSlot(ClothingItems item, String type,
                                String label, double w, double h) {
        StackPane slot = new StackPane();
        slot.setPrefSize(w, h); slot.setMaxSize(w, h); slot.setMinSize(w, h);

        if (item == null) {
            slot.setStyle("-fx-background-color:rgba(255,255,255,0.06);"
                    + "-fx-border-color:rgba(255,255,255,0.18);"
                    + "-fx-border-style:dashed;-fx-border-width:1.2;"
                    + "-fx-background-radius:10;-fx-border-radius:10;");
            Label ph = new Label(label);
            ph.setStyle("-fx-font-size:10px;-fx-text-fill:" + TXT_DIM + ";-fx-font-weight:bold;");
            ph.setWrapText(true);
            ph.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            ph.setMaxWidth(w - 10);
            slot.getChildren().add(ph);
        } else {
            String url = item.getImagePath();
            if (url != null) {
                ImageView iv = new ImageView(new Image(url, true));
                iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(true);
                slot.setStyle("-fx-background-color:transparent;");
                slot.getChildren().add(iv);
            } else {
                Rectangle swatch = new Rectangle(w, h);
                swatch.setArcWidth(10); swatch.setArcHeight(10);
                try { swatch.setFill(Color.web(colHex(item.getColour()))); }
                catch (Exception ex) { swatch.setFill(Color.LIGHTGRAY); }
                swatch.setOpacity(0.50);
                Label emojiL = new Label(typeEmoji(type));
                emojiL.setStyle("-fx-font-size:" + (int)(h * 0.30) + "px;");
                Label brandL = new Label(item.getBrand());
                brandL.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
                VBox info = new VBox(2, emojiL, brandL);
                info.setAlignment(Pos.CENTER);
                slot.setStyle("-fx-background-radius:10;-fx-background-color:rgba(0,0,0,0.10);");
                slot.getChildren().addAll(swatch, info);
            }
        }
        return slot;
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 5 — TRY COMBINATIONS (kept for reference)
    // ═════════════════════════════════════════════════════════════════════
    private void showTryCombinations() {
        StackPane bg = grad();
        Label title = screenTitle("Try Combinations");

        VBox fitCard = card(520, 400);
        fitCard.setAlignment(Pos.TOP_CENTER);
        Label fitHdr = new Label("✦  THE FIT  ✦");
        fitHdr.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
        fitCard.getChildren().add(fitHdr);

        boolean hasImg = refTopUrl != null || searchUrl != null || refBotUrl != null;
        if (hasImg) {
            if (refTopUrl != null || searchUrl != null) {
                String u = refTopUrl != null ? refTopUrl : searchUrl;
                ImageView iv = new ImageView(new Image(u, true));
                iv.setFitWidth(280); iv.setFitHeight(170); iv.setPreserveRatio(true);
                fitCard.getChildren().add(iv);
            }
            if (refBotUrl != null) {
                ImageView iv = new ImageView(new Image(refBotUrl, true));
                iv.setFitWidth(280); iv.setFitHeight(170); iv.setPreserveRatio(true);
                fitCard.getChildren().add(iv);
            }
        } else {
            VBox sum = new VBox(8); sum.setAlignment(Pos.CENTER);
            if (selTop   != null) sum.getChildren().add(fitItemLbl("👕  "+selTop.getBrand()+"  "+selTop.getColour()));
            if (selBot   != null) sum.getChildren().add(fitItemLbl("👖  "+selBot.getBrand()+"  "+selBot.getColour()));
            if (selShoe  != null) sum.getChildren().add(fitItemLbl("👟  "+selShoe.getBrand()+"  "+selShoe.getColour()));
            if (selOuter != null) sum.getChildren().add(fitItemLbl("🧥  "+selOuter.getBrand()));
            if (selAcc   != null) sum.getChildren().add(fitItemLbl("👜  "+selAcc.getBrand()));
            if (sum.getChildren().isEmpty())
                sum.getChildren().add(fitItemLbl("Select items in My Wardrobe first ✨"));
            fitCard.getChildren().add(sum);
        }

        VBox sugCard = card(370, 160);
        sugCard.getChildren().addAll(cardSec("SUGGESTIONS:"), wrapLabel(buildSuggestion()));

        final int[] ratingVal = {0};
        HBox stars = starRow(0, true);
        Label ratingLbl = new Label("Click a star to rate");
        ratingLbl.setStyle("-fx-font-size:11px;-fx-text-fill:" + TXT_LIGHT + ";");
        for (int i = 0; i < 5; i++) {
            final int s = i + 1;
            stars.getChildren().get(i).setOnMouseClicked(e -> {
                ratingVal[0] = s; updateStars(stars, s);
                ratingLbl.setText(s + " / 5  " + ratingWord(s));
                ArrayList<Outfit> all = outfitMgr.getAllOutfits();
                if (!all.isEmpty()) all.get(all.size()-1).setRating(s);
            });
        }
        VBox ratingCard = card(370, 110);
        ratingCard.getChildren().addAll(cardSec("RATING:"), stars, ratingLbl);

        Button savePng = actionBtn("SAVE AS PNG FILE");
        Button editBtn = actionBtn("EDIT OUTFIT");
        savePng.setOnAction(e -> alert("PNG export — integrate an image library to enable this."));
        editBtn.setOnAction(e -> fade(this::showWardrobe));

        HBox actionRow = new HBox(14, savePng, editBtn);
        actionRow.setAlignment(Pos.CENTER);

        VBox rightCol = new VBox(14, sugCard, ratingCard, actionRow);
        rightCol.setAlignment(Pos.TOP_LEFT); rightCol.setMaxWidth(390);

        HBox mainRow = new HBox(22, fitCard, rightCol);
        mainRow.setAlignment(Pos.CENTER);

        Button backBtn = backBtn(); backBtn.setOnAction(e -> fade(this::showWardrobe));
        VBox page = new VBox(18, title, mainRow);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(14, 30, 24, 30));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10,0,0,10));
        bg.getChildren().addAll(scrollWrap(page), backBtn);
        show(bg);
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 6 — OUTFIT SCHEDULER
    // ═════════════════════════════════════════════════════════════════════
    private void showScheduler() {
        StackPane bg = grad();
        Label title = screenTitle("Outfit Schedule");

        Button prevBtn = semiBtn("◀");
        Button nextBtn = semiBtn("▶");
        Label monthLbl = new Label();
        monthLbl.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");

        GridPane calGrid = new GridPane();
        calGrid.setHgap(6); calGrid.setVgap(6);
        calGrid.setAlignment(Pos.CENTER);

        Runnable buildCal = () -> {
            calGrid.getChildren().clear();
            monthLbl.setText(calMonth.getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "  " + calMonth.getYear());

            String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
            for (int d = 0; d < 7; d++) {
                Label dl = new Label(dayNames[d]);
                dl.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:" + TXT_DIM + ";");
                dl.setAlignment(Pos.CENTER); dl.setPrefWidth(110);
                calGrid.add(dl, d, 0);
            }

            int firstDay = calMonth.atDay(1).getDayOfWeek().getValue() % 7;
            int daysInMonth = calMonth.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                int col = (firstDay + day - 1) % 7;
                int row = (firstDay + day - 1) / 7 + 1;
                LocalDate date = calMonth.atDay(day);
                boolean hasO  = scheduler.hasOutfit(date);
                boolean today = date.equals(LocalDate.now());

                VBox cell = new VBox(4);
                cell.setPrefSize(110, 76);
                cell.setPadding(new Insets(6));
                cell.setAlignment(Pos.TOP_LEFT);
                cell.setCursor(javafx.scene.Cursor.HAND);

                String cellBg = today ? "rgba(255,255,255,0.30)"
                              : hasO  ? "rgba(204,255,0,0.20)"
                              :          CARD_BG;
                String cellBd = today ? "white"
                              : hasO  ? BTN_LIME
                              :          "rgba(255,255,255,0.20)";
                cell.setStyle("-fx-background-color:" + cellBg
                        + ";-fx-border-color:" + cellBd
                        + ";-fx-border-width:1.5;-fx-border-radius:10;-fx-background-radius:10;");

                Label dayLbl = new Label(String.valueOf(day));
                dayLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:"
                        + (today ? "white" : TXT_CREAM) + ";");
                cell.getChildren().add(dayLbl);

                if (hasO) {
                    Outfit first = scheduler.getOutfits(date).get(0);
                    Label outLbl = new Label(first.getName());
                    outLbl.setStyle("-fx-font-size:9px;-fx-text-fill:" + BTN_LIME + ";");
                    outLbl.setWrapText(true); outLbl.setMaxWidth(98);
                    cell.getChildren().add(outLbl);
                }

                cell.setOnMouseClicked(e -> showDayPopup(date, calGrid));
                calGrid.add(cell, col, row);
            }
        };

        buildCal.run();
        prevBtn.setOnAction(e -> { calMonth = calMonth.minusMonths(1); buildCal.run(); });
        nextBtn.setOnAction(e -> { calMonth = calMonth.plusMonths(1);  buildCal.run(); });

        HBox navRow = new HBox(18, prevBtn, monthLbl, nextBtn);
        navRow.setAlignment(Pos.CENTER);

        Label upcomingTitle = new Label("Upcoming outfits:");
        upcomingTitle.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
        VBox upcomingList = new VBox(8);
        refreshUpcomingList(upcomingList);

        Button backBtn = backBtn(); backBtn.setOnAction(e -> fade(this::showHome));

        VBox page = new VBox(18, title, navRow, calGrid, upcomingTitle, upcomingList);
        page.setAlignment(Pos.CENTER);
        page.setPadding(new Insets(20, 30, 30, 30));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(10,0,0,10));
        bg.getChildren().addAll(scrollWrap(page), backBtn);
        show(bg);
    }

    private void showDayPopup(LocalDate date, GridPane calGrid) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(date.toString());

        StackPane popBg = grad();
        popBg.setPrefSize(460, 400);

        Label title = new Label(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + ", " + date.getDayOfMonth() + " "
                + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        title.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");

        VBox existingList = new VBox(8);
        refreshExistingList(existingList, date);

        Label addLabel = new Label("Assign a saved outfit:");
        addLabel.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_LIGHT + ";");

        ComboBox<Outfit> outfitCB = new ComboBox<>();
        outfitCB.getItems().addAll(outfitMgr.getAllOutfits());
        outfitCB.setConverter(new javafx.util.StringConverter<Outfit>() {
            @Override public String toString(Outfit o)   { return o == null ? "" : o.getName(); }
            @Override public Outfit fromString(String s) { return null; }
        });
        outfitCB.setPrefWidth(280);
        outfitCB.setStyle("-fx-background-color:rgba(255,255,255,0.18);-fx-font-size:12px;"
                + "-fx-border-radius:8;-fx-background-radius:8;");

        Button assignBtn = pill("Assign Outfit", 170, 42);
        assignBtn.setOnAction(e -> {
            Outfit sel = outfitCB.getValue();
            if (sel == null) return;
            scheduler.addOutfit(date, sel);
            refreshExistingList(existingList, date);
            FileManager.saveAll(wardrobe, outfitMgr, scheduler);
        });

        Button closeBtn = pill("Close", 120, 40);
        closeBtn.setOnAction(e -> popup.close());

        VBox content = new VBox(14, title, existingList, addLabel,
                new HBox(10, outfitCB, assignBtn), closeBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));
        popBg.getChildren().add(content);

        popup.setScene(new Scene(popBg, 460, 400));
        popup.show();
    }

    private void refreshExistingList(VBox existingList, LocalDate date) {
        existingList.getChildren().clear();
        for (Outfit o : scheduler.getOutfits(date)) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 10, 6, 10));
            row.setStyle("-fx-background-color:rgba(255,255,255,0.14);-fx-background-radius:8;");
            Label oLbl = new Label(o.toString());
            oLbl.setStyle("-fx-font-size:11px;-fx-text-fill:" + TXT_CREAM + ";");
            oLbl.setWrapText(true);
            HBox.setHgrow(oLbl, Priority.ALWAYS);
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color:rgba(255,80,80,0.6);-fx-text-fill:white;"
                    + "-fx-background-radius:6;-fx-padding:2 7;-fx-cursor:hand;-fx-font-size:11px;");
            removeBtn.setOnAction(ev -> {
                scheduler.removeOutfit(date, o);
                refreshExistingList(existingList, date);
            });
            row.getChildren().addAll(oLbl, removeBtn);
            existingList.getChildren().add(row);
        }
        if (existingList.getChildren().isEmpty()) {
            Label none = new Label("No outfits scheduled for this day.");
            none.setStyle("-fx-font-size:11px;-fx-text-fill:" + TXT_DIM + ";");
            existingList.getChildren().add(none);
        }
    }

    private void refreshUpcomingList(VBox list) {
        list.getChildren().clear();
        LocalDate today = LocalDate.now();
        int shown = 0;
        for (int i = 0; i <= 30 && shown < 5; i++) {
            LocalDate d = today.plusDays(i);
            if (scheduler.hasOutfit(d)) {
                for (Outfit o : scheduler.getOutfits(d)) {
                    HBox row = new HBox(12);
                    row.setPadding(new Insets(8, 12, 8, 12));
                    row.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:10;");
                    Label dl = new Label(d.toString());
                    dl.setStyle("-fx-font-size:11px;-fx-text-fill:" + BTN_LIME + ";-fx-font-weight:bold;");
                    Label ol = new Label(o.getName());
                    ol.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_CREAM + ";");
                    row.getChildren().addAll(dl, ol);
                    list.getChildren().add(row);
                    shown++;
                }
            }
        }
        if (list.getChildren().isEmpty()) {
            Label none = new Label("No outfits scheduled in the next 30 days.");
            none.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_DIM + ";");
            list.getChildren().add(none);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // SCREEN 7 — ONLINE SEARCH
    // (SerpApi integration plugs in here — replace startBtn action)
    // ═════════════════════════════════════════════════════════════════════
    /*private void showOnlineSearch() {
        StackPane bg = grad();
        Label title = screenTitle("Online Search");

        ImageView prev = new ImageView();
        prev.setFitWidth(480); prev.setFitHeight(260);
        prev.setPreserveRatio(true); prev.setVisible(false);

        Label hint = new Label("CHOOSE FROM FOLDER OR DRAG IT HERE");
        hint.setStyle("-fx-font-size:14px;-fx-font-family:'Courier New';"
                + "-fx-text-fill:" + TXT_LIGHT + ";-fx-letter-spacing:1.5;");

        StackPane zone = dropCard(hint, prev, 580, 300);
        zone.setOnMouseClicked(e -> {
            File f = pickImg(); if (f == null) return;
            searchUrl = f.toURI().toString();
            showInDrop(prev, hint, searchUrl);
        });
        dnd(zone, prev, hint, u -> searchUrl = u);

        Button startBtn = pill("START SEARCHING", 260, 52);
        // ── TODO: replace this action with SerpApi call (Step 6 of API guide)
        startBtn.setOnAction(e -> {
            if (searchUrl != null) fade(this::showOuterAcc);
            else shake(zone);
        });

        Button backBtn = backBtn(); backBtn.setOnAction(e -> fade(this::showHome));
        VBox content = new VBox(24, title, zone, startBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24, 40, 30, 40));
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(12,0,0,12));
        bg.getChildren().addAll(scrollWrap(content), backBtn);
        show(bg);
    }*/

    private void showOnlineSearch() {
    StackPane bg = grad();
    Label title = screenTitle("Online Search");

    ImageView prev = new ImageView();
    prev.setFitWidth(480); prev.setFitHeight(260);
    prev.setPreserveRatio(true); prev.setVisible(false);

    Label hint = new Label("CHOOSE FROM FOLDER OR DRAG IT HERE");
    hint.setStyle("-fx-font-size:14px;-fx-font-family:'Courier New';"
            + "-fx-text-fill:" + TXT_LIGHT + ";-fx-letter-spacing:1.5;");

    StackPane zone = dropCard(hint, prev, 580, 300);
    zone.setOnMouseClicked(e -> {
        File f = pickImg(); if (f == null) return;
        searchUrl = f.toURI().toString();
        showInDrop(prev, hint, searchUrl);
    });
    dnd(zone, prev, hint, u -> searchUrl = u);

    // ── BUDGET INPUT SECTION ─────────────────────────────────────────────
    Label budgetLabel = new Label("💰 Maximum Budget (PKR):");
    budgetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TXT_CREAM + ";");
    
    TextField budgetField = new TextField();
    budgetField.setPromptText("e.g., 5000 (leave empty for any)");
    budgetField.setPrefWidth(250);
    budgetField.setStyle("-fx-background-color: rgba(255,255,255,0.18);"
            + "-fx-text-fill: white;-fx-prompt-text-fill: rgba(255,255,255,0.45);"
            + "-fx-border-color: rgba(255,255,255,0.30);-fx-border-radius: 8;"
            + "-fx-background-radius: 8;-fx-font-size: 14px;-fx-padding: 8 12;");
    
    // Quick budget buttons
    HBox quickBudget = new HBox(10);
    quickBudget.setAlignment(Pos.CENTER);
    String[] budgets = {"2000", "5000", "10000", "Any"};
    for (String b : budgets) {
        Button btn = new Button(b.equals("Any") ? "Any" : "Under " + b);
        btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;"
                + "-fx-background-radius: 15; -fx-padding: 5 12; -fx-font-size: 11px;");
        btn.setOnAction(ev -> {
            if (b.equals("Any")) {
                budgetField.clear();  // Clear the field, don't set "Any"
            } else {
                budgetField.setText(b);
            }
        });
        quickBudget.getChildren().add(btn);
    }
    
    VBox budgetBox = new VBox(8, budgetLabel, budgetField, quickBudget);
    budgetBox.setAlignment(Pos.CENTER);
    
    // ── STATUS LABEL ─────────────────────────────────────────────────────
    Label statusLabel = new Label();
    statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700;");
    statusLabel.setWrapText(true);
    statusLabel.setMaxWidth(500);
    statusLabel.setVisible(false);
    
    // ── RESULTS AREA ─────────────────────────────────────────────────────
    VBox resultsArea = new VBox(8);
    resultsArea.setStyle("-fx-background-color: " + CARD_BG + ";-fx-background-radius: 10;"
            + "-fx-padding: 10;");
    resultsArea.setVisible(false);
    ScrollPane resultsScroll = new ScrollPane(resultsArea);
    resultsScroll.setFitToWidth(true);
    resultsScroll.setPrefHeight(250);
    resultsScroll.setStyle("-fx-background-color: transparent;");

    Button startBtn = pill("START SEARCHING", 260, 52);
    
    // ── SEARCH BUTTON ACTION ────────────────────────────────────────────
    startBtn.setOnAction(e -> {
        if (searchUrl != null) {
            String budgetText = budgetField.getText().trim();
            double maxBudget;
            
            // Handle "Any" or empty input
            if (budgetText.isEmpty() || budgetText.equalsIgnoreCase("Any")) {
                maxBudget = Double.MAX_VALUE;  // No budget limit
            } else {
                try {
                    maxBudget = Double.parseDouble(budgetText);
                    if (maxBudget < 0) {
                        statusLabel.setText("❌ Budget cannot be negative!");
                        statusLabel.setVisible(true);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("❌ Invalid budget. Please enter a number (e.g., 5000) or leave empty.");
                    statusLabel.setVisible(true);
                    return;
                }
            }
            
            // Show status
            statusLabel.setText("🔍 Searching for similar items under Rs " + 
                    (maxBudget == Double.MAX_VALUE ? "any budget" : String.format("%.0f", maxBudget)));
            statusLabel.setVisible(true);
            resultsArea.setVisible(false);
            startBtn.setDisable(true);
            
            // Convert URI to file path
            String filePath = new File(java.net.URI.create(searchUrl)).getAbsolutePath();
            
            // Run search in background
            new Thread(() -> {
                SerpResult result = SerpApiClient.searchByImageWithBudget(filePath, maxBudget);
                
                javafx.application.Platform.runLater(() -> {
                    if (!result.success) {
                        statusLabel.setText("❌ Error: " + result.errorMessage);
                    } else if (result.matches.isEmpty()) {
                        statusLabel.setText("❌ No items found under Rs " + 
                                (maxBudget == Double.MAX_VALUE ? "any budget" : String.format("%.0f", maxBudget)) 
                                + ". Try a different image or higher budget!");
                    } else {
                        statusLabel.setText("✅ Found " + result.matches.size() + " matching items!");
                        displaySearchResults(resultsArea, result);
                        resultsArea.setVisible(true);
                    }
                    startBtn.setDisable(false);
                });
            }).start();
        } else {
            shake(zone);
        }
    });

    Button backBtn = backBtn(); 
    backBtn.setOnAction(e -> fade(this::showHome));
    
    // Add all components
    VBox content = new VBox(24, title, zone, budgetBox, startBtn, statusLabel, resultsScroll);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(24, 40, 30, 40));
    
    StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
    StackPane.setMargin(backBtn, new Insets(12,0,0,12));
    bg.getChildren().addAll(scrollWrap(content), backBtn);
    show(bg);
}

// Helper method to display search results
private void displaySearchResults(VBox resultsArea, SerpResult result) {
    resultsArea.getChildren().clear();
    
    Label header = new Label("📸 Similar items found:");
    header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TXT_CREAM + ";");
    resultsArea.getChildren().add(header);
    
    int count = 0;
    for (SerpResult.Match match : result.matches) {
        if (count >= 5) break;
        
        VBox itemBox = new VBox(5);
        itemBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 10;");
        
        Label titleLabel = new Label((count+1) + ". " + match.title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TXT_CREAM + ";");
        titleLabel.setWrapText(true);
        
        Label priceLabel = new Label("💰 " + (match.price.isEmpty() ? "Price unknown" : match.price));
        priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + BTN_LIME + ";");
        
        Label sourceLabel = new Label("🛍️ From: " + match.source);
        sourceLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TXT_LIGHT + ";");
        
        // Make link clickable
        Hyperlink link = new Hyperlink("🔗 View product");
        link.setStyle("-fx-text-fill: #CCFF00; -fx-font-size: 10px; -fx-cursor: hand;");
        final String url = match.link;
        link.setOnAction(ev -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ex) {
                alert("Could not open link: " + url);
            }
        });
        
        itemBox.getChildren().addAll(titleLabel, priceLabel, sourceLabel, link);
        resultsArea.getChildren().add(itemBox);
        count++;
    }
}

    // ==============================================================
// AI POPUP METHOD (for Wardrobe panel)
// ==============================================================
private void showAIPopup() {
    if (selTop == null || selBot == null || selShoe == null) {
        alert("Please select at least a Top, Bottom, and Shoes first!");
        return;
    }
    
    Stage popup = new Stage();
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setTitle("✨ AI Fashion Stylist");
    
    VBox popupContent = new VBox(15);
    popupContent.setAlignment(Pos.CENTER);
    popupContent.setPadding(new Insets(25));
    popupContent.setStyle("-fx-background-color: #C084FC; -fx-background-radius: 15;");
    popupContent.setPrefWidth(450);
    popupContent.setPrefHeight(380);
    
    Label title = new Label("✨ AI Stylist Advice ✨");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TXT_CREAM + ";");
    
    Label selectedLabel = new Label(getSelectedItemsSummary());
    selectedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TXT_LIGHT + ";");
    selectedLabel.setWrapText(true);
    
    Label loadingLabel = new Label("🤔 Click 'Get Advice' to see AI suggestions...");
    loadingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700;");
    loadingLabel.setWrapText(true);
    
    TextArea resultArea = new TextArea();
    resultArea.setEditable(false);
    resultArea.setWrapText(true);
    resultArea.setPrefHeight(160);
    resultArea.setStyle("-fx-background-color: #A855F7;"
            + "-fx-text-fill: " + TXT_CREAM + ";"
            + "-fx-font-size: 12px;"
            + "-fx-border-color: rgba(255,255,255,0.3);"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;");
    
    Button getAdviceBtn = new Button("✨ Get AI Advice");
    getAdviceBtn.setStyle("-fx-background-color:" + BTN_LIME + ";-fx-text-fill:" + BTN_LIME_TXT + ";"
            + "-fx-font-size:13px;-fx-font-weight:bold;"
            + "-fx-background-radius:20;-fx-padding:10 25;-fx-cursor:hand;");
    
    Button closeBtn = new Button("Close");
    closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2);-fx-text-fill: white;"
            + "-fx-background-radius:20;-fx-padding:8 20;-fx-cursor:hand;");
    closeBtn.setOnAction(ev -> popup.close());
    
    HBox buttonRow = new HBox(15, getAdviceBtn, closeBtn);
    buttonRow.setAlignment(Pos.CENTER);
    
    popupContent.getChildren().addAll(title, selectedLabel, loadingLabel, resultArea, buttonRow);
    
    getAdviceBtn.setOnAction(ev -> {
        loadingLabel.setText("🤔 AI is thinking about your outfit...");
        loadingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #FFD700;");
        resultArea.clear();
        getAdviceBtn.setDisable(true);
        
        String topColor = selTop != null ? selTop.getColour() : "Not selected";
        String bottomColor = selBot != null ? selBot.getColour() : "Not selected";
        String shoeColor = selShoe != null ? selShoe.getColour() : "Not selected";
        String outerColor = selOuter != null ? selOuter.getColour() : null;
        String accColor = selAcc != null ? selAcc.getColour() : null;
        
        new Thread(() -> {
            String advice = GroqClient.getSuggestion(
                topColor, bottomColor, shoeColor, outerColor, accColor, "current"
            );
            
            javafx.application.Platform.runLater(() -> {
                loadingLabel.setText("✅ Here's your personalized advice:");
                loadingLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + BTN_LIME + ";");
                resultArea.setText(advice);
                getAdviceBtn.setDisable(false);
            });
        }).start();
    });
    
    Scene popupScene = new Scene(popupContent);
    popup.setScene(popupScene);
    popup.show();
}

// ==============================================================
// COLLAPSIBLE AI SECTION (for Outerwear panel)
// ==============================================================
    private VBox createCollapsibleAISection() {
    // Main container - Purple background matching C_MID (#C084FC)
    VBox container = new VBox(0);
    container.setStyle("-fx-background-color: #C084FC;-fx-background-radius:12;");
    container.setPadding(new Insets(0));
    
    // Header (always visible - click to expand/collapse)
    HBox header = new HBox(8);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(12));
    header.setStyle("-fx-cursor:hand;");
    
    // Arrow Label
    Label arrowLabel = new Label("▶");
    arrowLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + BTN_LIME + ";");
    
    Label headerText = new Label(" AI Stylist Suggestions");
    headerText.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TXT_CREAM + ";");
    
    header.getChildren().addAll(arrowLabel, headerText);
    
    // Content (hidden initially)
    VBox content = new VBox(10);
    content.setPadding(new Insets(0, 12, 12, 12));
    content.setVisible(false);
    content.setManaged(false);
    
    // Loading indicator
    Label loadingLabel = new Label("🤔 Click 'Get Advice' to see AI suggestions...");
    loadingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TXT_LIGHT + ";");
    loadingLabel.setWrapText(true);
    
    // Result area - Darker purple for contrast
    TextArea adviceArea = new TextArea();
    adviceArea.setEditable(false);
    adviceArea.setWrapText(true);
    adviceArea.setPrefHeight(120);
    adviceArea.setStyle("-fx-background-color: #A855F7;"
            + "-fx-text-fill: " + TXT_CREAM + ";"
            + "-fx-font-size: 11px;"
            + "-fx-border-color: rgba(255,255,255,0.2);"
            + "-fx-border-radius: 8;"
            + "-fx-background-radius: 8;");
    
    // Get Advice button
    Button getAdviceBtn = new Button("✨ Get AI Advice");
    getAdviceBtn.setStyle("-fx-background-color:" + BTN_LIME + ";-fx-text-fill:" + BTN_LIME_TXT + ";"
            + "-fx-font-size:12px;-fx-font-weight:bold;"
            + "-fx-background-radius:20;-fx-padding:8 16;-fx-cursor:hand;");
    
    // Status label
    Label statusLabel = new Label();
    statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #FFD700;");
    statusLabel.setWrapText(true);
    
    content.getChildren().addAll(loadingLabel, adviceArea, getAdviceBtn, statusLabel);
    
    // Toggle expand/collapse when header is clicked
    header.setOnMouseClicked(e -> {
        boolean isExpanded = content.isVisible();
        content.setVisible(!isExpanded);
        content.setManaged(!isExpanded);
        arrowLabel.setText(isExpanded ? "▶" : "▼");
    });
    
    // Get Advice button action
    getAdviceBtn.setOnAction(e -> {
        if (selTop == null || selBot == null || selShoe == null) {
            statusLabel.setText("❌ Please select at least Top, Bottom, and Shoes first!");
            return;
        }
        
        loadingLabel.setText("🤔 AI is thinking about your outfit...");
        loadingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #FFD700;");
        adviceArea.clear();
        statusLabel.setText("");
        getAdviceBtn.setDisable(true);
        
        String topColor = selTop != null ? selTop.getColour() : "Not selected";
        String bottomColor = selBot != null ? selBot.getColour() : "Not selected";
        String shoeColor = selShoe != null ? selShoe.getColour() : "Not selected";
        String outerColor = selOuter != null ? selOuter.getColour() : null;
        String accColor = selAcc != null ? selAcc.getColour() : null;
        
        new Thread(() -> {
            String advice = GroqClient.getSuggestion(
                topColor, bottomColor, shoeColor, outerColor, accColor, "current"
            );
            
            javafx.application.Platform.runLater(() -> {
                loadingLabel.setText("✅ Here's your personalized advice:");
                loadingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + BTN_LIME + ";");
                adviceArea.setText(advice);
                getAdviceBtn.setDisable(false);
                
                if (!content.isVisible()) {
                    content.setVisible(true);
                    content.setManaged(true);
                    arrowLabel.setText("▼");
                }
            });
        }).start();
    });
    
    container.getChildren().addAll(header, content);
    return container;
}


    // ═════════════════════════════════════════════════════════════════════
    // GRID REFRESH
    // ═════════════════════════════════════════════════════════════════════
    private void refreshGrid() {
        if (rowTops == null) return;
        rowTops.getChildren().clear(); rowBots.getChildren().clear(); rowShoes.getChildren().clear();
        wardrobe.getTops()   .forEach(i -> rowTops .getChildren().add(itemCard(i,"top")));
        wardrobe.getBottoms().forEach(i -> rowBots .getChildren().add(itemCard(i,"bottom")));
        wardrobe.getShoes()  .forEach(i -> rowShoes.getChildren().add(itemCard(i,"shoes")));
        if (rowTops .getChildren().isEmpty()) rowTops .getChildren().add(emptyLbl("No tops yet — add items first"));
        if (rowBots .getChildren().isEmpty()) rowBots .getChildren().add(emptyLbl("No bottoms yet"));
        if (rowShoes.getChildren().isEmpty()) rowShoes.getChildren().add(emptyLbl("No shoes yet"));
    }

    private void refreshOuterAccGrid() {
        if (rowOuter == null) return;
        rowOuter.getChildren().clear(); rowAcc.getChildren().clear();
        wardrobe.getOuterwear()   .forEach(i -> rowOuter.getChildren().add(itemCard(i,"outer")));
        wardrobe.getAccessories() .forEach(i -> rowAcc  .getChildren().add(itemCard(i,"acc")));
        if (rowOuter.getChildren().isEmpty()) rowOuter.getChildren().add(emptyLbl("No outerwear yet"));
        if (rowAcc  .getChildren().isEmpty()) rowAcc  .getChildren().add(emptyLbl("No accessories yet"));
    }

    // ═════════════════════════════════════════════════════════════════════
    // ITEM CARD
    // ═════════════════════════════════════════════════════════════════════
    private VBox itemCard(ClothingItems item, String type) {
        Rectangle swatch = new Rectangle(110, 110);
        swatch.setArcWidth(12); swatch.setArcHeight(12);
        try { swatch.setFill(Color.web(colHex(item.getColour()))); }
        catch (Exception ex) { swatch.setFill(Color.LIGHTGRAY); }

        Label emojiLbl = new Label(typeEmoji(type));
        emojiLbl.setStyle("-fx-font-size:28px;");
        StackPane imgBox = new StackPane(swatch, emojiLbl);
        imgBox.setPrefSize(130, 130);

        String url = item.getImagePath();
        if (url != null) {
            ImageView iv = new ImageView(new Image(url, true));
            iv.setFitWidth(130); iv.setFitHeight(130); iv.setPreserveRatio(true);
            imgBox.getChildren().setAll(iv);
        }

        Label brandLbl = new Label(item.getBrand());
        brandLbl.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:white;");
        brandLbl.setMaxWidth(138);

        Label detLbl = new Label(item.getColour() + "  ·  " + item.getSize());
        detLbl.setStyle("-fx-font-size:10px;-fx-text-fill:" + TXT_LIGHT + ";");

        Label wearLbl = new Label("Worn " + item.getWearCount() + "×");
        wearLbl.setStyle("-fx-font-size:9px;-fx-text-fill:" + TXT_DIM + ";");

        VBox card = new VBox(6, imgBox, brandLbl, detLbl, wearLbl);
        card.setPrefWidth(152); card.setMaxWidth(152);
        card.setPadding(new Insets(10));
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle(cardStyle(false));

        card.setOnMouseClicked(e -> {
            boolean was = isSel(item, type);
            desel(type); clearRowStyle(type);
            if (!was) { sel(item, type); card.setStyle(cardStyle(true)); }
            if (type.equals("outer") || type.equals("acc")) {
                fade(this::showOuterAcc);
            } else {
                refreshPreview();
            }
        });
        card.setOnMouseEntered(e -> { if (!isSel(item,type)) card.setStyle(cardHov()); });
        card.setOnMouseExited(e  -> card.setStyle(cardStyle(isSel(item, type))));
        return card;
    }

    private VBox gridRow(String label, HBox items) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:"
                + TXT_DIM + ";-fx-letter-spacing:1.5;");
        ScrollPane sp = new ScrollPane(items);
        sp.setPrefHeight(182); sp.setMaxHeight(182);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        VBox row = new VBox(6, lbl, sp);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:14;");
        return row;
    }

    // ═════════════════════════════════════════════════════════════════════
    // SELECTION HELPERS
    // ═════════════════════════════════════════════════════════════════════
    private void sel(ClothingItems item, String type) {
        switch (type) {
            case "top"    -> selTop   = (top)         item;
            case "bottom" -> selBot   = (bottom)      item;
            case "shoes"  -> selShoe  = (shoes)       item;
            case "outer"  -> selOuter = (outerwear)   item;
            case "acc"    -> selAcc   = (accessories) item;
        }
    }
    private void desel(String type) {
        switch (type) {
            case "top"    -> selTop   = null;
            case "bottom" -> selBot   = null;
            case "shoes"  -> selShoe  = null;
            case "outer"  -> selOuter = null;
            case "acc"    -> selAcc   = null;
        }
    }
    private boolean isSel(ClothingItems item, String type) {
        return switch (type) {
            case "top"    -> selTop   == item;
            case "bottom" -> selBot   == item;
            case "shoes"  -> selShoe  == item;
            case "outer"  -> selOuter == item;
            case "acc"    -> selAcc   == item;
            default       -> false;
        };
    }
    private void clearRowStyle(String type) {
        HBox row = switch (type) {
            case "top"    -> rowTops;
            case "bottom" -> rowBots;
            case "shoes"  -> rowShoes;
            case "outer"  -> rowOuter;
            case "acc"    -> rowAcc;
            default       -> null;
        };
        if (row == null) return;
        for (Node n : row.getChildren())
            if (n instanceof VBox v) v.setStyle(cardStyle(false));
    }

    // ═════════════════════════════════════════════════════════════════════
    // BACKGROUND
    // ═════════════════════════════════════════════════════════════════════
    private StackPane grad() {
        StackPane pane = new StackPane();
        if (!BG_IMAGE_PATH.isEmpty()) {
            try {
                String url = new File(BG_IMAGE_PATH).toURI().toString();
                ImageView bg = new ImageView(new Image(url, true));
                bg.setPreserveRatio(false);
                bg.fitWidthProperty().bind(pane.widthProperty());
                bg.fitHeightProperty().bind(pane.heightProperty());
                Rectangle overlay = new Rectangle();
                overlay.widthProperty().bind(pane.widthProperty());
                overlay.heightProperty().bind(pane.heightProperty());
                overlay.setFill(Color.web("rgba(30,0,60,0.28)"));
                pane.getChildren().addAll(bg, overlay);
            } catch (Exception ex) {
                addGradRect(pane);
            }
        } else {
            addGradRect(pane);
        }
        return pane;
    }

    private void addGradRect(StackPane pane) {
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(pane.widthProperty());
        rect.heightProperty().bind(pane.heightProperty());
        rect.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, C_PINK), new Stop(0.5, C_MID), new Stop(1.0, C_PURPLE)));
        pane.getChildren().add(rect);
    }

    // ═════════════════════════════════════════════════════════════════════
    // UI FACTORY HELPERS
    // ═════════════════════════════════════════════════════════════════════
    private Label screenTitle(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:42px;-fx-font-weight:bold;-fx-font-family:'Georgia';"
                + "-fx-font-style:italic;-fx-text-fill:" + TXT_CREAM + ";"
                + "-fx-effect:dropshadow(gaussian,rgba(60,0,100,0.50),14,0,1,3);");
        return l;
    }

    private Button pill(String text, double w, double h) {
        Button b = new Button(text);
        b.setPrefWidth(w); b.setPrefHeight(h);
        b.setStyle("-fx-background-color:" + BTN_FILL + ";-fx-text-fill:white;"
                + "-fx-font-size:13px;-fx-font-weight:bold;"
                + "-fx-background-radius:30;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.25),6,0,0,2);");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + BTN_HOV + ";-fx-text-fill:white;"
                + "-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:30;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.35),8,0,0,3);"));
        b.setOnMouseExited(e  -> b.setStyle("-fx-background-color:" + BTN_FILL + ";-fx-text-fill:white;"
                + "-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:30;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.25),6,0,0,2);"));
        return b;
    }

    private Button semiBtn(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:rgba(255,255,255,0.18);-fx-text-fill:white;"
                + "-fx-font-size:12px;-fx-font-weight:bold;"
                + "-fx-background-radius:22;-fx-padding:8 18;-fx-cursor:hand;");
        return b;
    }

    private Button backBtn() {
        Button b = new Button("← Back");
        b.setStyle("-fx-background-color:rgba(255,255,255,0.20);-fx-text-fill:white;"
                + "-fx-font-size:12px;-fx-background-radius:18;-fx-padding:6 14;-fx-cursor:hand;");
        return b;
    }

    private Button actionBtn(String text) {
        Button b = new Button(text);
        b.setPrefWidth(168); b.setPrefHeight(50);
        b.setStyle("-fx-background-color:" + BTN_FILL + ";-fx-text-fill:white;"
                + "-fx-font-size:12px;-fx-font-weight:bold;-fx-background-radius:20;-fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + BTN_HOV
                + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:bold;"
                + "-fx-background-radius:20;-fx-cursor:hand;"));
        b.setOnMouseExited(e  -> b.setStyle("-fx-background-color:" + BTN_FILL
                + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:bold;"
                + "-fx-background-radius:20;-fx-cursor:hand;"));
        return b;
    }

    private VBox card(double w, double h) {
        VBox v = new VBox(10);
        v.setPrefSize(w, h); v.setMaxWidth(w);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:" + CARD_BG + ";-fx-background-radius:18;");
        return v;
    }

    private StackPane dropCard(Label hint, ImageView prev, double w, double h) {
        StackPane z = new StackPane(hint, prev);
        z.setPrefSize(w, h); z.setMaxSize(w, h);
        z.setCursor(javafx.scene.Cursor.HAND);
        z.setStyle("-fx-background-color:" + CARD_BG
                + ";-fx-background-radius:18;"
                + "-fx-border-color:rgba(255,255,255,0.35);"
                + "-fx-border-radius:18;-fx-border-style:dashed;-fx-border-width:1.5;");
        z.setOnMouseEntered(e -> z.setStyle("-fx-background-color:" + CARD_HOV
                + ";-fx-background-radius:18;"
                + "-fx-border-color:white;-fx-border-radius:18;-fx-border-style:dashed;-fx-border-width:2;"));
        z.setOnMouseExited(e  -> z.setStyle("-fx-background-color:" + CARD_BG
                + ";-fx-background-radius:18;"
                + "-fx-border-color:rgba(255,255,255,0.35);"
                + "-fx-border-radius:18;-fx-border-style:dashed;-fx-border-width:1.5;"));
        return z;
    }

    private String cardStyle(boolean sel) {
        return sel
            ? "-fx-background-color:" + CARD_SEL + ";-fx-border-color:white;-fx-border-width:2;"
              + "-fx-border-radius:14;-fx-background-radius:14;"
            : "-fx-background-color:rgba(255,255,255,0.12);-fx-border-color:rgba(255,255,255,0.20);"
              + "-fx-border-width:1;-fx-border-radius:14;-fx-background-radius:14;";
    }

    private String cardHov() {
        return "-fx-background-color:rgba(255,255,255,0.20);-fx-border-color:rgba(255,255,255,0.55);"
                + "-fx-border-width:1;-fx-border-radius:14;-fx-background-radius:14;";
    }

    private Label dim(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_DIM + ";");
        return l;
    }
    private Label cardSec(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + TXT_CREAM + ";");
        return l;
    }
    private Label fitItemLbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:13px;-fx-text-fill:" + TXT_CREAM + ";");
        l.setWrapText(true); return l;
    }
    private Label wrapLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_LIGHT + ";");
        l.setWrapText(true); return l;
    }
    private Label emptyLbl(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:" + TXT_DIM + ";-fx-padding:16 8 0 8;");
        return l;
    }

    private TextField field(String prompt, double w) {
        TextField tf = new TextField();
        tf.setPromptText(prompt); tf.setPrefWidth(w);
        tf.setStyle("-fx-background-color:rgba(255,255,255,0.18);"
                + "-fx-text-fill:white;-fx-prompt-text-fill:rgba(255,255,255,0.45);"
                + "-fx-border-color:rgba(255,255,255,0.30);-fx-border-radius:8;-fx-background-radius:8;"
                + "-fx-font-size:12px;-fx-padding:8 10;");
        return tf;
    }

    private ComboBox<String> cb(String... items) {
        ComboBox<String> c = new ComboBox<>();
        c.getItems().addAll(items); c.setValue(items[0]); c.setPrefWidth(175);
        c.setStyle("-fx-background-color:rgba(255,255,255,0.18);"
                + "-fx-border-color:rgba(255,255,255,0.30);-fx-border-radius:8;-fx-background-radius:8;"
                + "-fx-font-size:12px;");
        return c;
    }

    private void styleTog(ToggleButton tb, boolean on) {
        tb.setStyle(on
            ? "-fx-background-color:white;-fx-text-fill:#7C3AED;"
              + "-fx-background-radius:20;-fx-padding:7 18;-fx-font-size:12px;-fx-cursor:hand;"
            : "-fx-background-color:rgba(255,255,255,0.16);-fx-text-fill:white;"
              + "-fx-border-color:rgba(255,255,255,0.30);-fx-border-radius:20;-fx-background-radius:20;"
              + "-fx-padding:7 18;-fx-font-size:12px;-fx-cursor:hand;");
    }

    private VBox node(String label, Node n) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:rgba(255,255,255,0.65);");
        return new VBox(4, l, n);
    }

    private HBox row2(Node a, Node b) {
        HBox h = new HBox(12, a, b); h.setAlignment(Pos.CENTER_LEFT); return h;
    }
    private HBox hrow() {
        HBox h = new HBox(10); h.setPadding(new Insets(4)); h.setAlignment(Pos.CENTER_LEFT); return h;
    }

    private HBox starRow(int filled, boolean clickable) {
        HBox row = new HBox(4); row.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 5; i++) {
            Label s = new Label(i <= filled ? "★" : "☆");
            s.setStyle("-fx-font-size:22px;-fx-text-fill:" + (i<=filled?STAR_ON:STAR_OFF)
                    + (clickable?";-fx-cursor:hand;":";"));
            row.getChildren().add(s);
        }
        return row;
    }

    private void updateStars(HBox row, int filled) {
        for (int i = 0; i < row.getChildren().size(); i++) {
            Label s = (Label) row.getChildren().get(i);
            boolean on = (i+1) <= filled;
            s.setText(on?"★":"☆");
            s.setStyle("-fx-font-size:22px;-fx-text-fill:"+(on?STAR_ON:STAR_OFF)+";-fx-cursor:hand;");
        }
    }

    private ScrollPane scrollWrap(VBox content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private void dnd(StackPane zone, ImageView iv, Label hint,
                     java.util.function.Consumer<String> setter) {
        zone.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });
        zone.setOnDragDropped(e -> {
            if (e.getDragboard().hasFiles()) {
                String u = e.getDragboard().getFiles().get(0).toURI().toString();
                setter.accept(u); showInDrop(iv, hint, u);
            }
            e.consume();
        });
    }

    private void showInDrop(ImageView iv, Label hint, String url) {
        iv.setImage(new Image(url, true)); iv.setVisible(true); hint.setVisible(false);
    }

    private File pickImg() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose an image");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg","*.bmp"));
        return fc.showOpenDialog(root.getScene().getWindow());
    }

    private void show(Node s) { root.getChildren().setAll(s); }

    private void fade(Runnable next) {
        FadeTransition out = new FadeTransition(Duration.millis(140), root);
        out.setFromValue(1); out.setToValue(0);
        out.setOnFinished(e -> {
            next.run();
            FadeTransition in = new FadeTransition(Duration.millis(140), root);
            in.setFromValue(0); in.setToValue(1); in.play();
        });
        out.play();
    }

    private void shake(Node n) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), n);
        tt.setByX(10); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.setOnFinished(e -> n.setTranslateX(0)); tt.play();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.setTitle("SmartFit"); a.showAndWait();
    }

    private String stripEmoji(String s) { return s.replaceAll("^.+  ","").trim(); }

    private String buildSuggestion() {
        List<String> tips = new ArrayList<>();
        if (selTop != null && selBot != null)
            tips.add("• " + selTop.getColour() + " top pairs well with " + selBot.getColour() + " bottom");
        if (selShoe != null)
            tips.add("• Try tucking in your top with these " + selShoe.getColour() + " shoes");
        if (selOuter != null)
            tips.add("• The " + selOuter.getColour() + " outerwear completes the look");
        if (tips.isEmpty())
            tips.add("• Select items in My Wardrobe for personalised suggestions");
        return String.join("\n", tips);
    }

// ✅ ADD THIS NEW METHOD HERE
private String getSelectedItemsSummary() {
    StringBuilder sb = new StringBuilder("📋 Your selected items:\n");
    if (selTop != null) sb.append("  👕 Top: ").append(selTop.getColour()).append(" ").append(selTop.getBrand()).append("\n");
    if (selBot != null) sb.append("  👖 Bottom: ").append(selBot.getColour()).append(" ").append(selBot.getBrand()).append("\n");
    if (selShoe != null) sb.append("  👟 Shoes: ").append(selShoe.getColour()).append(" ").append(selShoe.getBrand()).append("\n");
    if (selOuter != null) sb.append("  🧥 Outerwear: ").append(selOuter.getColour()).append(" ").append(selOuter.getBrand()).append("\n");
    if (selAcc != null) sb.append("  👜 Accessory: ").append(selAcc.getColour()).append(" ").append(selAcc.getBrand()).append("\n");
    if (selOuter == null && selAcc == null) sb.append("  (Add outerwear/accessories in the next screen)\n");
    return sb.toString();
}

    private String ratingWord(int s) {
        return switch(s) {
            case 1 -> "Not great"; case 2 -> "Okay"; case 3 -> "Good";
            case 4 -> "Great!"; default -> "Perfect!";
        };
    }

    private String typeEmoji(String t) {
        return switch(t) {
            case "top" -> "👕"; case "bottom" -> "👖"; case "shoes" -> "👟";
            case "outer" -> "🧥"; case "acc" -> "👜"; default -> "👗";
        };
    }

    private String colHex(String c) {
        return switch(c.trim().toLowerCase()) {
            case "red"          -> "#E74C3C"; case "blue"   -> "#3498DB";
            case "black"        -> "#2C3E50"; case "white"  -> "#ECF0F1";
            case "green"        -> "#27AE60"; case "pink"   -> "#FF69B4";
            case "purple"       -> "#9B59B6"; case "yellow" -> "#F1C40F";
            case "orange"       -> "#E67E22"; case "grey", "gray" -> "#95A5A6";
            case "navy"         -> "#1A252F"; case "lavender" -> "#DDA0DD";
            case "lime"         -> "#ADFF2F"; case "brown"  -> "#8B4513";
            case "beige"        -> "#F5F0DC"; default -> "#BDC3C7";
        };
    }
}