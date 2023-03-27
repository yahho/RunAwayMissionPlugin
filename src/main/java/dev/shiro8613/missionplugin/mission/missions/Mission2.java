package dev.shiro8613.missionplugin.mission.missions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.event.EventEnum;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.stream.Collectors;

public class Mission2 extends Mission {

    private final ItemStack reward = new ItemStack(Material.POTION);

    private Player player = null;
    private BossBar bar;
    private int tickCounter;
    private final int ticks1min = 60*20;
    private final int sixMin = ticks1min*2;

    private void greet() {
        // タイトルを出す
        final Component title = Component.text("ミッション発動: 迷子の、、お知らせです。。。", NamedTextColor.YELLOW);
        final Component subtitle = Component.text("詳細はチャットを確認してください", NamedTextColor.GRAY, TextDecoration.ITALIC);
        getJavaPlugin().getServer().showTitle(Title.title(title, subtitle));

        // チャットに詳細を表示
        getJavaPlugin().getServer().sendMessage(Component.text("迷子のお知らせです。",NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD));
        getJavaPlugin().getServer().sendMessage(Component.text("ピンクのスカートを着て、サングラスを掛けた、白玉のような女の子を探しています。",NamedTextColor.YELLOW));
        getJavaPlugin().getServer().sendMessage(Component.text("もし見つけましたらその子と一緒に写真を撮り、写真を",NamedTextColor.YELLOW).append(Component.text("Discordの『#逃走中ミッション』チャンネル", NamedTextColor.GOLD)).clickEvent(ClickEvent.openUrl("https://discord.com/channels/1046066805552189440/1080796750303997973")).append(Component.text("にお貼りください。", NamedTextColor.YELLOW)));
        getJavaPlugin().getServer().sendMessage(Component.text("5人の逃走者が写真を貼ることができれば",NamedTextColor.YELLOW).append(Component.text("ミッション成功", NamedTextColor.GREEN)).append(Component.text("です。", NamedTextColor.YELLOW)));
        getJavaPlugin().getServer().sendMessage(Component.text("全員に",NamedTextColor.YELLOW).append(reward.displayName().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, true)).append(Component.text("をお渡しします。")));
        getJavaPlugin().getServer().sendMessage(Component.text("もし制限時間内に達成できなければ、",NamedTextColor.YELLOW).append(Component.text("逃走者全員が10秒間発光します", NamedTextColor.RED, TextDecoration.UNDERLINED)).append(Component.text("。", NamedTextColor.YELLOW)));
    }
    @Override
    public void Init() {
        // 報酬ポーションの情報をセットする
        PotionMeta pm = (PotionMeta) reward.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.SPEED));
        reward.setItemMeta(pm);

        // メッセージ送信
        greet();

        // dangomushi codes(modified)
        player = getPlayers().get(0);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,1,1);
        bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
        tickCounter = 0;
        bar.addPlayer(player);
        bar.setVisible(true);
    }

    @Override
    public void Tick() {

        if (tickCounter == sixMin) {
            // 失敗(タイムアップ)時の処理
            bar.removeAll();
            final Component failTitle = Component.text("ミッション失敗", NamedTextColor.RED);
            final Component failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に発光エフェクトが付与されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);
            getJavaPlugin().getServer().showTitle(Title.title(failTitle, failSubTitle));
            List<Player> challengers = getPlayers().stream().filter(p -> p.getScoreboard().getTeams().stream().anyMatch(sc -> sc.getName().equals("challenger"))).collect(Collectors.toList());
            for (Player p : challengers) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10*20, 1,false, false));
                p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
            }
            missionEnd();
            return;
        }
        else if (tickCounter < sixMin) {
            // 進行中の処理
            tickCounter++;
            if (tickCounter % 20 == 0) {
                bar.setTitle("残り時間: " + (((sixMin - tickCounter) < ticks1min) ? ((sixMin - tickCounter)/20 + "秒") : ((sixMin - tickCounter)/ticks1min + "分"+ ((sixMin - tickCounter)%ticks1min)/20 +"秒")));
            }
            //TODO: 成功時の処理
        }

        bar.setProgress(1.0 - ((double) tickCounter/sixMin));
    }
}
