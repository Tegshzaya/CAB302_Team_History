# Design System Style Guide

## Overview
This guide shows the colors, fonts, and styles used in the Historical Art Guesser app. We wanted to create a clean, modern look that's easy to use and looks professional.

## Colors We Use

### Main Colors
- **Primary Blue**: #667eea - Used for main buttons and important elements
- **Secondary Blue**: #f093fb - Used for secondary buttons and accents
- **Ghost Button**: rgba(255, 255, 255, 0.6) - Used for subtle buttons with white text

### Background Colors
- **Main Background**: #f0f4f8 - Light blue-gray background for the whole app
- **Card Background**: #ffffff - Pure white for cards and content areas
- **Form Background**: #ffffff - White background for forms and inputs

### Text Colors
- **Main Text**: #0f172a - Dark text for headings and important content
- **Body Text**: #1e293b - Slightly lighter for regular text
- **Secondary Text**: #334155 - Even lighter for less important text
- **Caption Text**: #475569 - Lightest text for labels and captions

### Status Colors
- **Success**: #22c55e - Green for correct answers and success messages
- **Error**: #ef4444 - Red for wrong answers and error messages
- **Warning**: #f59e0b - Orange for warnings

## Typography

### Font Family
We use "Inter" as the main font, with fallbacks to system fonts like "Segoe UI" and "Roboto".

### Text Sizes
- **Large Headings**: 32px, bold (700 weight)
- **Medium Headings**: 24px, semi-bold (600 weight)
- **Small Headings**: 18px, semi-bold (600 weight)
- **Body Text**: 16px, regular (400 weight)
- **Small Text**: 14px, regular (400 weight)
- **Caption Text**: 12px, medium (500 weight)

## Button Styles

### Primary Buttons
- Background: #667eea (blue)
- Text: White
- Padding: 18px 28px
- Border radius: 16px
- Min width: 220px, min height: 52px
- Has shadow effect and hover animations

### Secondary Buttons
- Background: #f093fb (pink)
- Text: White
- Padding: 16px 24px
- Border radius: 16px
- Min width: 180px, min height: 48px

### Ghost Buttons
- Background: rgba(255, 255, 255, 0.6) (semi-transparent white)
- Text: #1a202c (dark)
- Border: 1px solid rgba(255, 255, 255, 0.8)
- Padding: 16px 24px
- Min width: 200px, min height: 48px

### Button Sizes
- **Large**: 280px wide, 56px tall, 18px padding
- **Medium**: 200px wide, 44px tall, 16px padding
- **Small**: 120px wide, 36px tall, 12px padding

## Game Interface Colors

### Answer Buttons
- **Default**: White background, dark text, light border
- **Hover**: Light gray background, blue border
- **Selected**: Light blue background, blue text and border
- **Correct**: Light green background, green text and border
- **Wrong**: Light red background, red text and border

### Feedback Messages
- **Correct**: Green text (#16a34a)
- **Wrong**: Red text (#dc2626)
- **Info**: Dark text (#374151)

## Spacing

### Common Spacing Values
- **Small**: 8px - For tight spacing
- **Medium**: 16px - For normal spacing
- **Large**: 24px - For section spacing
- **Extra Large**: 32px - For major spacing

### Padding
- **Button Padding**: 12px to 18px depending on size
- **Card Padding**: 24px
- **Form Padding**: 16px

## Design Principles

### Keep It Simple
- Use clean, uncluttered layouts
- Don't overuse colors or effects
- Make sure text is easy to read

### Be Consistent
- Use the same colors throughout the app
- Keep button styles consistent
- Use the same spacing patterns

### Make It Accessible
- Ensure good contrast between text and backgrounds
- Use clear, readable fonts
- Make buttons large enough to click easily

### Modern Look
- Use rounded corners (12px to 16px border radius)
- Add subtle shadows for depth
- Include smooth hover animations

## Implementation Notes

### CSS Classes
We use classes like:
- `.btn-primary` for main buttons
- `.btn-secondary` for secondary buttons
- `.btn-ghost` for subtle buttons
- `.btn-large`, `.btn-medium`, `.btn-small` for different sizes

### Text Wrapping
All buttons have text wrapping enabled so long text doesn't get cut off:
- `-fx-wrap-text: true`
- `-fx-text-overrun: ellipsis`

### Responsive Design
- Buttons adapt to different screen sizes
- Text scales appropriately
- Layouts work on different devices

This design system helps us maintain a consistent, professional look throughout the app while keeping it user-friendly and accessible.