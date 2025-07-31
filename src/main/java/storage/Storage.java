package main.java.storage;

import java.awt.image.BufferedImage;
import main.java.util.SpriteUtil;

public class Storage {
    public static final String[] pseudos = {
            "AlphaRomeo", "BravoTango", "CharlieSierra", "DeltaOscar", "EchoNovember", "FoxtrotMike", "GolfLima",
            "HotelKilo",
            "IndiaJuliet", "JulietIndia", "KiloHotel", "LimaGolf", "MikeFoxtrot", "NovemberEcho", "OscarDelta",
            "PapaCharlie",
            "QuebecBravo", "RomeoAlpha", "SierraQuebec", "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform",
            "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo",
            "EchoCharlie",
            "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet",
            "MikeKilo",
            "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo",
            "UniformSierra",
            "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu",
            "CharlieAlpha",
            "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel",
            "KiloIndia",
            "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa",
            "SierraQuebec",
            "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray",
            "AlphaYankee",
            "BravoZulu", "CharlieAlpha", "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho", "HotelFoxtrot",
            "IndiaGolf",
            "JulietHotel", "KiloIndia", "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike", "PapaNovember",
            "QuebecOscar",
            "RomeoPapa", "SierraQuebec", "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform", "X-rayVictor",
            "YankeeWhiskey",
            "ZuluX-ray", "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo", "EchoCharlie", "FoxtrotDelta",
            "GolfEcho",
            "HotelFoxtrot", "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet", "MikeKilo", "NovemberLima",
            "OscarMike",
            "PapaNovember", "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo", "UniformSierra", "VictorTango",
            "WhiskeyUniform",
            "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo",
            "EchoCharlie",
            "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet",
            "MikeKilo",
            "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo",
            "UniformSierra",
            "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu",
            "CharlieAlpha",
            "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel",
            "KiloIndia",
            "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa",
            "SierraQuebec",
            "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray",
            "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho",
            "HotelFoxtrot",
            "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike",
            "PapaNovember",
            "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform",
            "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo",
            "EchoCharlie",
            "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet",
            "MikeKilo",
            "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo",
            "UniformSierra",
            "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu",
            "CharlieAlpha",
            "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel",
            "KiloIndia",
            "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa",
            "SierraQuebec",
            "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray",
            "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho",
            "HotelFoxtrot",
            "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike",
            "PapaNovember",
            "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform",
            "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu", "CharlieAlpha", "DeltaBravo",
            "EchoCharlie",
            "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel", "KiloIndia", "LimaJuliet",
            "MikeKilo",
            "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa", "SierraQuebec", "TangoRomeo",
            "UniformSierra",
            "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray", "AlphaYankee", "BravoZulu",
            "CharlieAlpha",
            "DeltaBravo", "EchoCharlie", "FoxtrotDelta", "GolfEcho", "HotelFoxtrot", "IndiaGolf", "JulietHotel",
            "KiloIndia",
            "LimaJuliet", "MikeKilo", "NovemberLima", "OscarMike", "PapaNovember", "QuebecOscar", "RomeoPapa",
            "SierraQuebec",
            "TangoRomeo", "UniformSierra", "VictorTango", "WhiskeyUniform", "X-rayVictor", "YankeeWhiskey", "ZuluX-ray"
    };

    public static String SERVER_ADDRESS = "148.253.122.47";
    public static final int SERVER_PORT = 8080;

    /* Images */
    public static BufferedImage APP_ICON;
    public static BufferedImage SELECT_BG;
    public static BufferedImage HOME_BG;
    public static BufferedImage SETTINGS_BG;
    public static BufferedImage GAME_BG;
    public static BufferedImage RULES_BG;

    public static void loadImage() {
        APP_ICON = SpriteUtil.loadBufferedImage("APP_ICON.png");
        SELECT_BG = SpriteUtil.loadBufferedImage("BACKGROUND", "SELECT_BG.png");
        HOME_BG = SpriteUtil.loadBufferedImage("BACKGROUND", "HOME_BG.png");
        SETTINGS_BG = SpriteUtil.loadBufferedImage("BACKGROUND", "SETTINGS_BG.png");
        GAME_BG = SpriteUtil.loadBufferedImage("BACKGROUND", "GAME_BG.png");
        RULES_BG = SpriteUtil.loadBufferedImage("BACKGROUND", "RULES_BG.png");
    }

    /* GAME */
    public final static int ENDGAME_SCORE = 50;
}
