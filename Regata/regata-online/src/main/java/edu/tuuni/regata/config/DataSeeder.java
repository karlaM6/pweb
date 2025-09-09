package edu.tuuni.regata.config;


import edu.tuuni.regata.domain.*;
import edu.tuuni.regata.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.util.*;


@Component
public class DataSeeder implements CommandLineRunner {
    private final PlayerRepository playerRepo;
    private final BoatModelRepository modelRepo;
    private final BoatRepository boatRepo;
    private final GameMapRepository mapRepo;


    public DataSeeder(PlayerRepository p, BoatModelRepository m, BoatRepository b, GameMapRepository g){
        this.playerRepo = p; this.modelRepo = m; this.boatRepo = b; this.mapRepo = g;
    }


    @Override
    public void run(String... args) {
        if (playerRepo.count()+modelRepo.count()+boatRepo.count()+mapRepo.count() > 0) return;


// 1) Mapa jugable simple 15x8
        String[] rows = {
                "P ",
                " XXX ",
                " XX ",
                " XX ",
                " XXXX ",
                " ",
                " XX ",
                " M"
        };
        GameMap map = new GameMap();
        map.setName("Mapa Demo");
        map.setWidth(rows[0].length());
        map.setHeight(rows.length);
        map.setGrid(String.join("\n", rows));
        mapRepo.save(map);


// 2) Jugadores (5)
        List<Player> players = new ArrayList<>();
        for (int i=1;i<=5;i++){
            Player p = new Player();
            p.setUsername("player"+i);
            p.setDisplayName("Player "+i);
            players.add(playerRepo.save(p));
        }


// 3) Modelos (10)
        List<BoatModel> models = new ArrayList<>();
        for (int i=1;i<=10;i++){
            BoatModel m = new BoatModel();
            m.setName("Model-"+i);
            String hex = String.format("#%06X", (i*999999)&0xFFFFFF);
            m.setColorHex(hex);
            models.add(modelRepo.save(m));
        }


// 4) Barcos (50; 10 por jugador), v=(0,0), p=(0,0)
        Random r = new Random(42);
        for (Player p : players){
            for (int i=0;i<10;i++){
                Boat b = new Boat();
                b.setOwner(p);
                b.setModel(models.get(r.nextInt(models.size())));
                b.setVx(0); b.setVy(0); b.setX(0); b.setY(0);
                boatRepo.save(b);
            }
        }
    }
}