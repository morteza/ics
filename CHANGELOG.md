# ITRC Cyber Security Evaluation Tools - Change Log

The following items are change logs divided by dates. In addition to this list, please review git commits for more items and detailed descriptions.

# August 15, 2015

- Push footer to the bottom of the view.
- Initial support to import survey flow CSV file.
- Update surveys on save.
- Separate questions and flow designer.
- New results view at the end of participating in a survey.
- Update top menu in the survey designer.
- Update survey designer and survey executor action names.
- Two different survey types: regular, and standard. Both available and listed on dashboard.

# July 20, 2015

- CVSS 3.0 temporal questions, and respective UI.
- Action to calculate CVSS severity based on the score.

## July 18, 2015

- Several UI improvements and bug fixes.
- Initial  CVSS 3.0 calculator.
- Calculate CVSS 3.0 base score and show it to the user.

## July 15, 2015

- RedHat OpenShift deployment.

## July 10, 2015

- Rewrite homepage, and improve top menu.
- New page break block in survey flow.
 

## July 5, 2015

- New UI based on Semantic-UI v2.
- Several fixes and improvements to make the whole general structure runnable using `play run` command.
- New RTL theme, enabled automatically by local user language.
- Enhanced i18n, and respective API to change local user language.
- i18n for Farsi, and English.
- Update login page i18n messages.
- Add switch language item to the top menu.
- Farsi fonts for several UI components (It's Iranian Serif open source font).
- Improve reset password page, and change its segment to 'clearing segment'.
- Footer i18n messages.
- Add status page javascript.
- Improve dashboard for surveys.
- Modal to create a new survey from dashboard.
- List published surveys on dashboard.
- Add isDeleted and deletedAt fields to the general data model.
- Survey data model.
- i18n for since() extension.
- Improve Semantic-UI RTL font.
- Javascript Masonry library (v3) to sort out survey cards on dashboard.
- Initial survey designer to demo.
- Charts in the survey designer results page.
- Simple Security Calculator, and its view, to be implemented using AngularJs.

## July 3, 2015

- Initial commit.
- Security module.
- General application structure.
- OpenShift deployment configurations.
- Initial survey data model.
- General account management views and actions.
- Basic account roles.
- General views and layouts.
