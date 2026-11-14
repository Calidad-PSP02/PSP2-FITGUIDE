/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package omr.uacm.sistemafitguide;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX App
 */

public class CatalogoEjercicios {

    public static List<Ejercicio> obtenerRutina(String musculo, String nivel, boolean usaMancuernas) {
        List<Ejercicio> lista = new ArrayList<>();

        // Normalizamos los textos
        String m = musculo.toLowerCase();
        String n = nivel.toLowerCase();


        // EJERCICIOS DE BRAZO
        if (m.equals("brazo")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new Ejercicio("Flexiones en pared", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexion_pared.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/pared_pre.png", "Apoya las manos en la pared, flexiona los codos acercando el pecho y empuja.", 15));
                    lista.add(new Ejercicio("Flexiones cobra", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/flexiones_cobra.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/flexiones_cobra_pre.png", "Boca abajo, empuja con las manos para levantar el pecho. Mantén la cadera en el suelo.", 12));
                    lista.add(new Ejercicio("Plancha antebrazo a mano", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/basico/plancha_antebrazo_mano.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/basico/plancha_ab_pre.png", "Desde plancha de antebrazos, sube apoyando una mano a la vez hasta estirar los brazos.", 12));
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new Ejercicio("Flexiones elevadas", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_elevadas.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/elevadas_pre.png", "Apoya manos en una superficie elevada y haz la flexión manteniendo el cuerpo recto.", 15));
                    lista.add(new Ejercicio("Flexiones diamante", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/FlexDiamante.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "Junta las manos bajo el pecho y baja con los codos pegados al torso.", 15));
                    lista.add(new Ejercicio("Flexiones cerradas", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/intermedio/flexiones_cerradas.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/medio/flex_cerradas_pre.png", "Coloca tus manos juntas, y al comento de hacer la flexion separalas ", 15));
                } else if (n.contains("avanzado")) {
                    lista.add(new Ejercicio("Flexiones a una mano", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/avanzado/a_una_mano.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/una_mano_pre.png", "Realiza tus flexiones normales pero apoyando todo tu peso en una sola mano.", 10));
                    lista.add(new Ejercicio("Flexiones en pared", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/avanzado/flexiones_en_pared.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/pared_abajo_pre.png", "Coloca tus pies en la pared y tus manos en el piso, bajando poco a poco.", 8));
                    lista.add(new Ejercicio("Plancha full brazo", "/omr/uacm/sistemafitguide/videos/peso_propio/brazo/avanzado/plancha_full_brazo.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pp/avanzado/plancha_full_pre.png", "Apoya tus manos en el piso y elevate quedando todo tu cuerpo elevado.", 8));
                }
            } else { // Con Peso Extra / Mancuernas
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new Ejercicio("Caminata", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/caminata.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/granjero_pre.png", "Camina manteniendo el equilibrio y la postura recta de forma controlada.", 15));
                    lista.add(new Ejercicio("Curl de bíceps", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_de_biseps.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_pre.png", "Flexiona los codos subiendo las mancuernas a los hombros. No muevas los brazos.", 12));
                    lista.add(new Ejercicio("Elevación de antebrazo", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/elevacion_antebrazo.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_ante_pre.png", "Apoya antebrazos en una superficie y levanta las mancuernas usando solo las muñecas.", 15));
                    lista.add(new Ejercicio("Curl martillo", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/basico/curl_martillo.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/basico/curl_mar_pre.png", "Coloca tu brazo a la altura de tu cade y levanta el peso hasta la altura de tu cuello.", 15));
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new Ejercicio("Curl sostenido", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/intermedio/curl_sostenido.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/curl_sos_pre.png", "Flexiona los codos a 90 grados con las mancuernas y mantén la posición fija.", 15));
                    lista.add(new Ejercicio("Curl concentrado", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/intermedio/curl_concentrado.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/curl_consent_pre.png", "Sentado, apolla tu codo en tu pierna y levanta el peso.", 12));
                    lista.add(new Ejercicio("Squeeze ", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/intermedio/squeeze_press.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/squeezee.png", "Acostado, levanta las pesas hasta tener tu brazo por completo estirado", 12));
                    lista.add(new Ejercicio("Zottman ", "/omr/uacm/sistemafitguide/videos/peso_extra/brazo/intermedio/zottman.gif", "/omr/uacm/sistemafitguide/preview/Brazo/pe/medio/zottman_pre.png", "Coloca tu brazo a la altura de tu cadera, levanta el peso y cuando llegues a la altura de tu cuello gira tu muñeca", 12));
                } else if (n.contains("avanzado")) {

                }
            }
        }

        // EJERCICIOS DE ABDOMEN
        else if (m.equals("abdomen")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new Ejercicio("Crunch abdominal", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/crinch_abdo.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/crunch_pre.png", "Boca arriba con rodillas flexionadas, eleva los hombros contrayendo el abdomen.", 15));
                    lista.add(new Ejercicio("Elevación de piernas", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/elevacion_de_piernas.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/elevacion_pre.png", "Levanta piernas rectas a 90 grados y bájalas lento sin tocar el piso.", 15));
                    lista.add(new Ejercicio("Plancha", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/basico/plancha.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/basico/plancha_pre.png", "Apoya los antebrazos y mantén todo el cuerpo en línea recta.", 15));
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new Ejercicio("V-Ups (Abdominales en V)", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/intermedio/V_UPS.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/v_pre.png", "Eleva el torso y las piernas a la vez, intentando tocar las puntas de tus pies.", 12));
                    lista.add(new Ejercicio("Crunch bicicleta", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/intermedio/bicicleta_alterna.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/bici_pre.png", "Acostado, lleva tu codo derecho hacia tu rodilla izquierda y alterna fluidamente.", 15));
                    lista.add(new Ejercicio("Plancha con rotacion", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/intermedio/plancha_con_rotacion.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/medio/rot_pre.png", "En posición de plancha, eleva tu brazo lo mas que puedas con rotacion a ese mismo brazo.", 12));
                } else if (n.contains("avanzado")) {
                    lista.add(new Ejercicio("Bandera ", "/omr/uacm/sistemafitguide/videos/peso_propio/abdomen/avanzado/bandera.gif", "/omr/uacm/sistemafitguide/preview/Abdomen/pp/avanzado/bandera_pre.png", "Apoyate con la pared y eleva tus pierna para queda como una bandera.", 12));
                }
            } else { // Con Peso Extra / Mancuernas
                // en preparacion
            }
        }

        // EJERCICIOS DE PIERNA
        else if (m.equals("pierna")) {
            if (!usaMancuernas) { // Peso Propio
                if (n.contains("basico") || n.contains("básico")) {
                    lista.add(new Ejercicio("Sentadilla", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/sentadilla.gif", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/sentadilla_pre.png", "Baja la cadera como si fueras a sentarte, con la espalda recta.", 15));
                    lista.add(new Ejercicio("Zancadas", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/basico/zancadas.gif", "/omr/uacm/sistemafitguide/preview/Pierna/pp/basico/zancadas_pre.png", "Da un paso al frente y flexiona ambas rodillas a 90 grados.", 15));
                } else if (n.contains("intermedio") || n.contains("medio")) {
                    lista.add(new Ejercicio("Sentadilla búlgara", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/intermedio/sentadilla_bulgara.gif", "/omr/uacm/sistemafitguide/preview/Pierna/pp/medio/sentadilla_bulgara_pre.png", "Apoya un pie atrás en una superficie elevada y flexiona la pierna delantera.", 12));
                    lista.add(new Ejercicio("Zancadas con salto", "/omr/uacm/sistemafitguide/videos/peso_propio/pierna/intermedio/senta_salto.gif", "/omr/uacm/sistemafitguide/preview/Pierna/pp/medio/sentasalto_pre.png", "Realiza zancadas alternas impulsándote con un salto explosivo.", 12));
                } else if (n.contains("avanzado")) {
                    // En preparación para avanzado de pierna
                }
            } else { // Con Peso Extra / Mancuernas
                // En preparación
            }
        }

        return lista;
    }
}
