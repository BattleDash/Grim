package ac.grim.grimac.utils.blockplace;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.collisions.AxisUtil;
import ac.grim.grimac.utils.nmsutil.Materials;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
import com.github.retrooper.packetevents.protocol.world.states.defaulttags.ItemTags;
import com.github.retrooper.packetevents.protocol.world.states.enums.Attachment;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import org.bukkit.GameMode;

// Holy shit mojang stop reusing packets like this
// for fucks sake there are several desyncs AGAIN???
// HOW DIFFICULT CAN IT BE TO TELL THE SERVER THAT YOU RANG A BELL, AND NOT CREATE A GHOST BLOCK???
public class ConsumesBlockPlace {
    public static boolean consumesPlace(GrimPlayer player, WrappedBlockState state, BlockPlace place) {
        // Hey look, it's another DESYNC MOJANG
        if (state.getType() == StateTypes.BELL) {
            return goodBellHit(state, place);
        }
        if (BlockTags.CANDLE_CAKES.contains(state.getType())) {
            WrappedBlockState cake = StateTypes.CAKE.createBlockState();
            cake.setBites(1);
            place.set(cake);
            return true;
        }
        if (state.getType() == StateTypes.CAKE) {
            if (state.getBites() == 0 && place.getMaterial() != null) {
                place.set(StateTypes.CANDLE_CAKE);
                return true;
            }

            if (player.gamemode == GameMode.CREATIVE || (player.bukkitPlayer != null && player.bukkitPlayer.getFoodLevel() < 20)) {
                if (state.getBites() != 6) {
                    state.setBites(state.getBites() + 1);
                    place.set(state);
                } else {
                    place.set(StateTypes.AIR);
                }
                return true;
            }

            return false;
        }
        if (state.getType() == StateTypes.CAVE_VINES || state.getType() == StateTypes.CAVE_VINES_PLANT) {
            if (state.isBerries()) {
                state.setBerries(false);
                place.set(state);
                return true;
            }
            return false;
        }
        if (state.getType() == StateTypes.SWEET_BERRY_BUSH) {
            if (state.getAge() != 3 && place.getItemStack().getType() == ItemTypes.BONE_MEAL) {
                return false;
            } else if (state.getAge() > 1) {
                state.setAge(1);
                place.set(state);
                return true;
            } else {
                return false;
            }
        }
        if (state.getType() == StateTypes.TNT) {
            return place.getItemStack().getType() == ItemTypes.FIRE_CHARGE || place.getItemStack().getType() == ItemTypes.FLINT_AND_STEEL;
        }
        if (state.getType() == StateTypes.RESPAWN_ANCHOR) {
            if (place.getItemStack().getType() == ItemTypes.GLOWSTONE) {
                return true;
            }
            return player.getInventory().getOffHand().getType() != ItemTypes.GLOWSTONE;
        }
        if (state.getType() == StateTypes.COMMAND_BLOCK || state.getType() == StateTypes.CHAIN_COMMAND_BLOCK ||
                state.getType() == StateTypes.REPEATING_COMMAND_BLOCK || state.getType() == StateTypes.JIGSAW
                || state.getType() == StateTypes.JIGSAW) {
            // Where is the permission level???? Check for >= 2 level eventually... no API for this.
            // Only affects OP players, will fix eventually (also few desyncs from no minecraft lag compensation)
            return player.bukkitPlayer != null && player.bukkitPlayer.isOp() && player.gamemode == GameMode.CREATIVE;
        }
        if (state.getType() == StateTypes.COMPOSTER) {
            if (Materials.isCompostable(place.getItemStack().getType()) && state.getLevel() < 8) {
                return true;
            }
            return state.getLevel() == 8;
        }
        if (state.getType() == StateTypes.JUKEBOX) {
            return state.isHasRecord();
        }
        if (state.getType() == StateTypes.LECTERN) {
            if (state.isHasBook()) return true;
            return ItemTags.LECTERN_BOOKS.contains(place.getItemStack().getType());
        }

        return false;
    }

    private static boolean goodBellHit(WrappedBlockState bell, BlockPlace place) {
        BlockFace direction = place.getDirection();
        return isProperHit(bell, direction, place.getHitData().getRelativeBlockHitLocation().getY());
    }

    private static boolean isProperHit(WrappedBlockState bell, BlockFace direction, double p_49742_) {
        if (direction != BlockFace.UP && direction != BlockFace.DOWN && !(p_49742_ > (double) 0.8124F)) {
            BlockFace dir = bell.getFacing();
            Attachment attachment = bell.getAttachment();
            BlockFace dir2 = BlockFace.valueOf(direction.name());

            switch (attachment) {
                case FLOOR:
                    return AxisUtil.isSameAxis(dir, dir2);
                case SINGLE_WALL:
                case DOUBLE_WALL:
                    return !AxisUtil.isSameAxis(dir, dir2);
                case CEILING:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}
