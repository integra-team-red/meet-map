import {definePreset, palette} from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';


export const MeetMapTheme =
  definePreset(Aura, {
    primitive: {
      indigo: palette('#6366F1'),
      lavender: palette('#A3B3FF')
    },
    semantic: {
      primary: palette('{indigo}'),
      secondary: palette('{lavender}'),
      content: {borderRadius: '16px'},
      formField: {
        borderRadius: '16px',
        paddingY: '0.5rem',
      },
      navigation: {item: {borderRadius: '16px'}},

    },
    components: {
      tag: {
        root: {
          fontWeight: "600",
          padding: '0.3rem 0.7rem',
        },
      },
    }
  });

export const THEMES ={
  'meet-map':{label:'Meet Map', preset: MeetMapTheme},
  'aura': {label:'Aura', preset: Aura}
} as const;

export type ThemeName = keyof typeof THEMES;
export const THEME_STORAGE_KEY = 'app-theme';

export function storedThemeName(): ThemeName {
  const saved = localStorage.getItem('app-theme');
  if (saved === null)
    return 'meet-map';
  return saved as ThemeName;
}
