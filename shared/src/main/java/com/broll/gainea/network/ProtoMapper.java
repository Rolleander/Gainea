package com.broll.gainea.network;

import com.broll.gainea.net.*;

import java.util.List;

/** Converts generated wire messages into the mutable models still used by the UI. */
public final class ProtoMapper {
    private ProtoMapper() {}

    private interface Mapper<P, L> { L map(P value); }

    private static <P, L> L[] map(List<P> values, L[] target, Mapper<P, L> mapper) {
        for (int i = 0; i < values.size(); i++) target[i] = mapper.map(values.get(i));
        return target;
    }

    private static short[] shorts(List<Integer> values) {
        short[] result = new short[values.size()];
        for (int i = 0; i < values.size(); i++) result[i] = values.get(i).shortValue();
        return result;
    }

    private static byte[] bytes(List<Integer> values) {
        byte[] result = new byte[values.size()];
        for (int i = 0; i < values.size(); i++) result[i] = values.get(i).byteValue();
        return result;
    }

    private static int[] ints(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) result[i] = values.get(i);
        return result;
    }

    public static NT_Action action(com.broll.gainea.net.proto.NT_Action value) {
        NT_Action result;
        switch (value.getValueCase()) {
            case ATTACK: {
                com.broll.gainea.net.proto.NT_Action_Attack source = value.getAttack();
                NT_Action_Attack target = new NT_Action_Attack();
                target.units = units(source.getUnitsList());
                target.location = (short) source.getLocation();
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            case CARD: {
                com.broll.gainea.net.proto.NT_Action_Card source = value.getCard();
                NT_Action_Card target = new NT_Action_Card();
                target.cardId = source.getCardId();
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            case MOVE: {
                com.broll.gainea.net.proto.NT_Action_Move source = value.getMove();
                NT_Action_Move target = new NT_Action_Move();
                target.units = units(source.getUnitsList());
                target.location = (short) source.getLocation();
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            case PLACE_UNIT: {
                com.broll.gainea.net.proto.NT_Action_PlaceUnit source = value.getPlaceUnit();
                NT_Action_PlaceUnit target = new NT_Action_PlaceUnit();
                target.unitToPlace = source.hasUnitToPlace() ? unit(source.getUnitToPlace()) : null;
                target.possibleLocations = shorts(source.getPossibleLocationsList());
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            case SELECT_CHOICE: {
                com.broll.gainea.net.proto.NT_Action_SelectChoice source = value.getSelectChoice();
                NT_Action_SelectChoice target = new NT_Action_SelectChoice();
                target.choices = source.getChoicesList().toArray(new String[0]);
                target.objectChoices = new Object[source.getObjectChoicesCount()];
                for (int i = 0; i < target.objectChoices.length; i++) {
                    com.broll.gainea.net.proto.NT_Choice choice = source.getObjectChoices(i);
                    switch (choice.getValueCase()) {
                        case INTEGER_VALUE: target.objectChoices[i] = choice.getIntegerValue(); break;
                        case GOAL: target.objectChoices[i] = goal(choice.getGoal()); break;
                        case CARD: target.objectChoices[i] = card(choice.getCard()); break;
                        case UNIT: target.objectChoices[i] = unit(choice.getUnit()); break;
                        default: target.objectChoices[i] = null;
                    }
                }
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            case SHOP: {
                com.broll.gainea.net.proto.NT_Action_Shop source = value.getShop();
                NT_Action_Shop target = new NT_Action_Shop();
                target.index = source.getIndex();
                target.actionId = source.getActionId();
                result = target;
                break;
            }
            default: throw new IllegalArgumentException("Action has no concrete value");
        }
        return result;
    }

    public static NT_Action[] actions(List<com.broll.gainea.net.proto.NT_Action> values) {
        return map(values, new NT_Action[values.size()], ProtoMapper::action);
    }

    public static NT_Unit unit(com.broll.gainea.net.proto.NT_Unit value) {
        if (value.hasMonster()) return monster(value.getMonster());
        NT_Unit result = new NT_Unit();
        copyUnit(value, result);
        return result;
    }

    private static void copyUnit(com.broll.gainea.net.proto.NT_Unit value, NT_Unit result) {
        result.owner = (short) value.getOwner(); result.building = value.getBuilding();
        result.name = value.getName(); result.scale = value.getScale(); result.icon = (short) value.getIcon();
        result.id = (short) value.getId(); result.description = value.getDescription(); result.location = (short) value.getLocation();
        result.health = (short) value.getHealth(); result.maxHealth = (short) value.getMaxHealth();
        result.power = (short) value.getPower(); result.kills = (short) value.getKills(); result.type = (byte) value.getType();
    }

    private static NT_Monster monster(com.broll.gainea.net.proto.NT_Monster value) {
        NT_Monster result = new NT_Monster();
        result.owner = (short) value.getOwner(); result.building = value.getBuilding(); result.name = value.getName();
        result.scale = value.getScale(); result.icon = (short) value.getIcon(); result.id = (short) value.getId();
        result.description = value.getDescription(); result.location = (short) value.getLocation();
        result.health = (short) value.getHealth(); result.maxHealth = (short) value.getMaxHealth();
        result.power = (short) value.getPower(); result.kills = (short) value.getKills(); result.type = (byte) value.getType();
        result.stars = (byte) value.getStars(); result.actionTimer = (byte) value.getActionTimer(); result.behavior = (byte) value.getBehavior();
        return result;
    }

    public static NT_Unit[] units(List<com.broll.gainea.net.proto.NT_Unit> values) {
        return map(values, new NT_Unit[values.size()], ProtoMapper::unit);
    }

    public static NT_BoardObject boardObject(com.broll.gainea.net.proto.NT_BoardObject value) {
        if (value.hasMonster()) return monster(value.getMonster());
        if (value.hasUnit()) return unit(value.getUnit());
        NT_BoardObject result = new NT_BoardObject();
        result.owner = (short) value.getOwner(); result.building = value.getBuilding(); result.name = value.getName();
        result.scale = value.getScale(); result.icon = (short) value.getIcon(); result.id = (short) value.getId();
        result.description = value.getDescription(); result.location = (short) value.getLocation();
        return result;
    }

    public static NT_BoardObject[] boardObjects(List<com.broll.gainea.net.proto.NT_BoardObject> values) {
        return map(values, new NT_BoardObject[values.size()], ProtoMapper::boardObject);
    }

    public static NT_BoardEffect boardEffect(com.broll.gainea.net.proto.NT_BoardEffect value) {
        NT_BoardEffect result = new NT_BoardEffect();
        result.effect = value.getEffect(); result.id = value.getId(); result.info = value.getInfo();
        result.x = value.getX(); result.y = value.getY();
        return result;
    }

    public static NT_BoardEffect[] boardEffects(List<com.broll.gainea.net.proto.NT_BoardEffect> values) {
        return map(values, new NT_BoardEffect[values.size()], ProtoMapper::boardEffect);
    }

    public static NT_Player player(com.broll.gainea.net.proto.NT_Player value) {
        NT_Player result = new NT_Player();
        result.fraction = (byte) value.getFraction(); result.points = (byte) value.getPoints(); result.cards = (byte) value.getCards();
        result.id = (short) value.getId(); result.color = (byte) value.getColor(); result.stars = (short) value.getStars();
        result.name = value.getName(); result.units = units(value.getUnitsList());
        return result;
    }

    public static NT_Player[] players(List<com.broll.gainea.net.proto.NT_Player> values) {
        return map(values, new NT_Player[values.size()], ProtoMapper::player);
    }

    public static NT_Card card(com.broll.gainea.net.proto.NT_Card value) {
        NT_Card result = new NT_Card();
        result.id = (short) value.getId(); result.picture = (short) value.getPicture(); result.title = value.getTitle();
        result.text = value.getText(); result.playable = value.getPlayable(); result.event = value.getEvent();
        return result;
    }

    public static NT_Card[] cards(List<com.broll.gainea.net.proto.NT_Card> values) {
        return map(values, new NT_Card[values.size()], ProtoMapper::card);
    }

    public static NT_Goal goal(com.broll.gainea.net.proto.NT_Goal value) {
        NT_Goal result = new NT_Goal();
        result.description = value.getDescription(); result.restriction = value.getRestriction(); result.points = value.getPoints();
        result.progression = value.getProgression(); result.progressionGoal = value.getProgressionGoal();
        result.locations = ints(value.getLocationsList()); result.id = value.getId();
        return result;
    }

    public static NT_Goal[] goals(List<com.broll.gainea.net.proto.NT_Goal> values) {
        return map(values, new NT_Goal[values.size()], ProtoMapper::goal);
    }

    private static NT_Shop shop(com.broll.gainea.net.proto.NT_Shop value) {
        NT_Shop result = new NT_Shop();
        result.items = new NT_ShopItem[value.getItemsCount()];
        for (int i = 0; i < result.items.length; i++) {
            com.broll.gainea.net.proto.NT_ShopItem source = value.getItems(i);
            NT_ShopItem item;
            if (source.hasUnit()) {
                NT_ShopUnit unit = new NT_ShopUnit(); unit.unit = unit(source.getUnit().getUnit()); item = unit;
            } else {
                NT_ShopOther other = new NT_ShopOther();
                if (source.hasOther()) other.description = source.getOther().getDescription();
                item = other;
            }
            item.price = (short) source.getPrice();
            result.items[i] = item;
        }
        return result;
    }

    private static void board(com.broll.gainea.net.proto.NT_StartGame value, NT_StartGame result) {
        result.round = (short) value.getRound(); result.turn = (short) value.getTurn();
        result.players = players(value.getPlayersList()); result.objects = boardObjects(value.getObjectsList());
        result.effects = boardEffects(value.getEffectsList()); result.expansionsSetting = value.getExpansionsSetting();
        result.pointLimit = value.getPointLimit(); result.roundLimit = value.getRoundLimit();
        result.cards = cards(value.getCardsList()); result.shop = value.hasShop() ? shop(value.getShop()) : new NT_Shop();
    }

    public static NT_StartGame start(com.broll.gainea.net.proto.NT_StartGame value) {
        NT_StartGame result = new NT_StartGame(); board(value, result); return result;
    }

    public static NT_ReconnectGame reconnect(com.broll.gainea.net.proto.NT_ReconnectGame value) {
        NT_ReconnectGame result = new NT_ReconnectGame();
        result.round = (short) value.getRound(); result.turn = (short) value.getTurn();
        result.players = players(value.getPlayersList()); result.objects = boardObjects(value.getObjectsList()); result.effects = boardEffects(value.getEffectsList());
        result.expansionsSetting = value.getExpansionsSetting(); result.pointLimit = value.getPointLimit(); result.roundLimit = value.getRoundLimit();
        result.cards = cards(value.getCardsList()); result.shop = value.hasShop() ? shop(value.getShop()) : new NT_Shop(); result.goals = goals(value.getGoalsList());
        return result;
    }

    public static NT_BoardUpdate boardUpdate(com.broll.gainea.net.proto.NT_BoardUpdate value) {
        NT_BoardUpdate result = new NT_BoardUpdate();
        result.round = (short) value.getRound(); result.turn = (short) value.getTurn(); result.players = players(value.getPlayersList());
        result.objects = boardObjects(value.getObjectsList()); result.effects = boardEffects(value.getEffectsList()); return result;
    }

    public static NT_GameOver gameOver(com.broll.gainea.net.proto.NT_GameOver value) {
        NT_GameOver result = new NT_GameOver();
        result.round = (short) value.getRound(); result.turn = (short) value.getTurn(); result.players = players(value.getPlayersList());
        result.objects = boardObjects(value.getObjectsList()); result.effects = boardEffects(value.getEffectsList()); return result;
    }

    public static NT_GameStatistic statistic(com.broll.gainea.net.proto.NT_GameStatistic value) {
        NT_GameStatistic result = new NT_GameStatistic(); result.rounds = new NT_RoundStatistic[value.getRoundsCount()];
        for (int i = 0; i < result.rounds.length; i++) {
            com.broll.gainea.net.proto.NT_RoundStatistic source = value.getRounds(i); NT_RoundStatistic round = new NT_RoundStatistic();
            round.total = shorts(source.getTotalList()); round.locations = bytes(source.getLocationsList());
            round.fightingPower = shorts(source.getFightingPowerList()); round.units = bytes(source.getUnitsList()); result.rounds[i] = round;
        }
        return result;
    }

    public static NT_Lib library(com.broll.gainea.net.proto.NT_Lib value) {
        NT_Lib result = new NT_Lib();
        result.cards = new NT_Lib_Card[value.getCardsCount()];
        for (int i = 0; i < result.cards.length; i++) { com.broll.gainea.net.proto.NT_Lib_Card s=value.getCards(i); NT_Lib_Card t=new NT_Lib_Card(); t.title=s.getTitle(); t.text=s.getText(); t.picture=(short)s.getPicture(); t.type=(short)s.getType(); t.drawChance=s.getDrawChance(); t.directlyPlayed=s.getDirectlyPlayed(); result.cards[i]=t; }
        result.fractions = new NT_Lib_Fraction[value.getFractionsCount()];
        for (int i = 0; i < result.fractions.length; i++) { com.broll.gainea.net.proto.NT_Lib_Fraction s=value.getFractions(i); NT_Lib_Fraction t=new NT_Lib_Fraction(); t.index=s.getIndex(); t.name=s.getName(); t.desc=s.getDesc(); t.commander=unit(s.getCommander()); t.soldier=unit(s.getSoldier()); t.descPositive=s.getDescPositiveList().toArray(new String[0]); t.descNegative=s.getDescNegativeList().toArray(new String[0]); result.fractions[i]=t; }
        result.monsters = new NT_Lib_Monster[value.getMonstersCount()];
        for (int i = 0; i < result.monsters.length; i++) { com.broll.gainea.net.proto.NT_Lib_Monster s=value.getMonsters(i); NT_Lib_Monster t=new NT_Lib_Monster(); t.stars=(byte)s.getStars(); t.behavior=(byte)s.getBehavior(); t.health=(short)s.getHealth(); t.power=(short)s.getPower(); t.name=s.getName(); t.icon=(short)s.getIcon(); t.description=s.getDescription(); result.monsters[i]=t; }
        result.goals = new NT_Lib_Goal[value.getGoalsCount()];
        for (int i = 0; i < result.goals.length; i++) { com.broll.gainea.net.proto.NT_Lib_Goal s=value.getGoals(i); NT_Lib_Goal t=new NT_Lib_Goal(); t.description=s.getDescription(); t.restriction=s.getRestriction(); t.points=(byte)s.getPoints(); result.goals[i]=t; }
        return result;
    }

    public static NT_Battle_Roll battleRoll(com.broll.gainea.net.proto.NT_Battle_Roll value) {
        NT_Battle_Roll result = new NT_Battle_Roll(); result.sourceUnit=value.getSourceUnit(); result.number=value.getNumber();
        result.plus=value.getPlus(); result.min=value.getMin(); result.max=value.getMax(); return result;
    }

    public static NT_Battle_Roll[] battleRolls(List<com.broll.gainea.net.proto.NT_Battle_Roll> values) {
        return map(values, new NT_Battle_Roll[values.size()], ProtoMapper::battleRoll);
    }

    public static NT_Battle_Damage battleDamage(com.broll.gainea.net.proto.NT_Battle_Damage value) {
        NT_Battle_Damage result = new NT_Battle_Damage(); result.source=value.getSource(); result.target=value.getTarget(); result.lethal=value.getLethal(); return result;
    }

    public static NT_Battle_Damage[] battleDamage(List<com.broll.gainea.net.proto.NT_Battle_Damage> values) {
        return map(values, new NT_Battle_Damage[values.size()], ProtoMapper::battleDamage);
    }
}
